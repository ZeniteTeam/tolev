package com.br.startup.tolevBack.analysis.application.service;

import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft.AchadoDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft.EntidadeDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft.VariavelDraft;
import com.br.startup.tolevBack.analysis.internal.entity.Analise;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseEntidade;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseImpacto;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseResultado;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseResultadoVariavel;
import com.br.startup.tolevBack.analysis.internal.enums.StatusAnalise;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseEntidadeRepository;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseImpactoRepository;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseRepository;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseResultadoRepository;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseResultadoVariavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Grava o resultado de um analisador, sobrescrevendo a análise do dia.
 *
 * <p>Uma linha por tipo por dia: o usuário que lança dez transações num sábado
 * não gera dez pontos no gráfico de evolução, gera um — o do sábado, com o
 * número mais recente. O histórico continua servindo para a série temporal e
 * para contar recorrência de achado.
 */
@Service
@RequiredArgsConstructor
public class AnalysisPersistenceService {

    private final IAnaliseRepository analiseRepository;
    private final IAnaliseResultadoRepository resultadoRepository;
    private final IAnaliseResultadoVariavelRepository variavelRepository;
    private final IAnaliseEntidadeRepository entidadeRepository;
    private final IAnaliseImpactoRepository impactoRepository;

    @Transactional
    public Analise salvar(Long idUsuario, AnalysisDraft draft) {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDoDia = hoje.atStartOfDay();
        LocalDateTime fimDoDia = hoje.atTime(LocalTime.MAX);

        Analise analise = analiseRepository
                .findFirstByIdUsuarioAndTipoAndDataCriacaoBetween(
                        idUsuario, draft.tipo(), inicioDoDia, fimDoDia)
                .orElseGet(() -> Analise.builder()
                        .idUsuario(idUsuario)
                        .tipo(draft.tipo())
                        .build());

        analise.setOrigem("MOTOR_ANALISE");
        analise.setResultadoResumo(draft.resumo());
        analise.setRelevancia(draft.relevancia());
        analise.setStatus(StatusAnalise.FINALIZADA);
        analise.setPeriodoInicio(draft.periodoInicio());
        analise.setPeriodoFim(draft.periodoFim());
        analise.setAcionavel(draft.acionavel());
        // Carimba a hora do recálculo mesmo reaproveitando a linha: a data é o
        // que diz "esse número é de hoje" na tela.
        analise.setDataCriacao(LocalDateTime.now());

        analise = analiseRepository.save(analise);

        salvarResultado(analise, draft);
        salvarEntidades(analise, draft.entidades());
        salvarImpactos(analise, draft.achados());

        return analise;
    }

    private void salvarResultado(Analise analise, AnalysisDraft draft) {
        if (draft.resultado() == null) {
            return;
        }
        AnalysisDraft.ResultadoDraft r = draft.resultado();

        AnaliseResultado resultado = resultadoRepository.findByAnalise(analise)
                .orElseGet(() -> AnaliseResultado.builder().analise(analise).build());

        resultado.setClassificacao(r.classificacao());
        resultado.setScore(r.score());
        resultado.setProbabilidade(r.probabilidade());
        resultado.setCoeficienteGeral(r.coeficienteGeral());
        resultado.setNivelRisco(r.nivelRisco());
        resultado.setModeloUtilizado(r.modeloUtilizado());
        resultado.setVersaoModelo(r.versaoModelo());
        resultado.setExplicacao(r.explicacao());
        resultado.setDataCriacao(LocalDateTime.now());

        AnaliseResultado salvo = resultadoRepository.save(resultado);

        // Variáveis são substituídas inteiras: o conjunto muda de execução para
        // execução (uma categoria some da lista, outra entra) e casar linha a
        // linha custaria mais do que reescrever.
        variavelRepository.deleteByAnaliseResultado(salvo);
        variavelRepository.flush();

        List<AnaliseResultadoVariavel> variaveis = draft.variaveis().stream()
                .map(v -> paraEntidade(salvo, v))
                .toList();
        variavelRepository.saveAll(variaveis);
    }

    private AnaliseResultadoVariavel paraEntidade(AnaliseResultado resultado, VariavelDraft v) {
        return AnaliseResultadoVariavel.builder()
                .analiseResultado(resultado)
                .nomeVariavel(v.nome())
                .valorVariavel(v.valor())
                .valorFaixa(v.valorFaixa())
                .peso(v.peso())
                .coeficiente(v.coeficiente())
                .impactoResultado(v.impactoResultado())
                .faixaReferencia(v.faixaReferencia())
                .dataRegistro(LocalDate.now())
                .build();
    }

    private void salvarEntidades(Analise analise, List<EntidadeDraft> entidades) {
        entidadeRepository.deleteByAnalise(analise);
        entidadeRepository.flush();

        List<AnaliseEntidade> novas = entidades.stream()
                .map(e -> AnaliseEntidade.builder()
                        .analise(analise)
                        .tipoEntidade(e.tipoEntidade())
                        .idEntidade(e.idEntidade())
                        .papelEntidade(e.papelEntidade())
                        .pesoEntidade(e.pesoEntidade())
                        .build())
                .toList();
        entidadeRepository.saveAll(novas);
    }

    private void salvarImpactos(Analise analise, List<AchadoDraft> achados) {
        impactoRepository.deleteByAnalise(analise);
        impactoRepository.flush();

        List<AnaliseImpacto> impactos = achados.stream()
                .map(a -> AnaliseImpacto.builder()
                        .analise(analise)
                        .regra(a.regra().name())
                        .tipoImpacto(a.regra().tipoImpacto())
                        .entidadeOrigemTipo(a.entidadeOrigemTipo())
                        .entidadeOrigemId(a.entidadeOrigemId())
                        .entidadeImpactadaTipo(a.entidadeImpactadaTipo())
                        .entidadeImpactadaId(a.entidadeImpactadaId())
                        .descricao(a.descricao())
                        .gravidade(a.gravidade() != null ? a.gravidade().name() : null)
                        .scoreImpacto(a.scoreImpacto())
                        .impactoEstimadoValor(a.impactoEstimadoValor())
                        .impactoTemporalMensal(a.impactoMensal())
                        .impactoTemporalAnual(a.impactoAnual())
                        .build())
                .toList();
        impactoRepository.saveAll(impactos);
    }
}
