package com.br.startup.tolevBack.analysis.application.service;

import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft.AchadoDraft;
import com.br.startup.tolevBack.analysis.internal.config.AnalysisProperties;
import com.br.startup.tolevBack.analysis.internal.entity.Analise;
import com.br.startup.tolevBack.analysis.internal.entity.Recomendacao;
import com.br.startup.tolevBack.analysis.internal.entity.RecomendacaoEntidade;
import com.br.startup.tolevBack.analysis.internal.enums.NivelRisco;
import com.br.startup.tolevBack.analysis.internal.enums.Prioridade;
import com.br.startup.tolevBack.analysis.internal.enums.StatusRecomendacao;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseImpactoRepository;
import com.br.startup.tolevBack.analysis.internal.repository.IRecomendacaoEntidadeRepository;
import com.br.startup.tolevBack.analysis.internal.repository.IRecomendacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Decide quais achados já merecem virar recomendação.
 *
 * <p>Dois filtros em série, e o achado precisa passar nos dois:
 *
 * <ol>
 *   <li><b>Recorrência</b> — apareceu em pelo menos {@code ocorrenciasMinimas}
 *       análises distintas dentro da janela. Como há uma análise por tipo por
 *       dia, isso equivale a dias distintos. Filtra o mês atípico: a viagem, o
 *       presente de aniversário, o conserto do carro.</li>
 *   <li><b>Impacto</b> — o custo anual projetado passa de
 *       {@code impactoAnualMinimo}. Filtra o que é real mas irrelevante:
 *       recomendar cortar R$ 4 por mês gasta a atenção do usuário à toa.</li>
 * </ol>
 *
 * <p>O valor comparado é o custo <em>anual projetado</em> do último cálculo, não
 * a soma dos dias. Somar diariamente contaria o mesmo estouro mensal três vezes
 * só porque o usuário abriu o app três dias seguidos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationEngine {

    private final IRecomendacaoRepository recomendacaoRepository;
    private final IRecomendacaoEntidadeRepository recomendacaoEntidadeRepository;
    private final IAnaliseImpactoRepository impactoRepository;
    private final RecommendationTextService textoService;
    private final AnalysisProperties properties;

    @Transactional
    public List<Recomendacao> avaliar(Long idUsuario, Analise analise, List<AchadoDraft> achados) {
        LocalDateTime desde = LocalDateTime.now().minusDays(properties.janelaRecorrenciaDias());

        return achados.stream()
                .filter(achado -> qualifica(idUsuario, achado, desde))
                .filter(achado -> !jaExiste(idUsuario, achado))
                .map(achado -> criar(idUsuario, analise, achado))
                .toList();
    }

    private boolean qualifica(Long idUsuario, AchadoDraft achado, LocalDateTime desde) {
        long ocorrencias = achado.entidadeOrigemId() != null
                ? impactoRepository.contarOcorrencias(
                        idUsuario, achado.regra().name(), achado.entidadeOrigemId(), desde)
                : impactoRepository.contarOcorrenciasSemEntidade(
                        idUsuario, achado.regra().name(), desde);

        if (ocorrencias < properties.ocorrenciasMinimas()) {
            return false;
        }

        BigDecimal impactoAnual = Calculo.nz(achado.impactoAnual());
        if (impactoAnual.compareTo(properties.impactoAnualMinimo()) < 0) {
            return false;
        }

        log.debug("Achado {} qualificado para o usuário {}: {} ocorrências, R$ {}/ano",
                achado.regra(), idUsuario, ocorrencias, impactoAnual);
        return true;
    }

    /**
     * PENDENTE e ACEITA já estão na tela do usuário. IGNORADA foi recusada — não
     * se insiste. CONCLUIDA volta a valer depois do cooldown: o problema pode
     * ter reaparecido meses depois, e aí a recomendação faz sentido de novo.
     */
    private boolean jaExiste(Long idUsuario, AchadoDraft achado) {
        LocalDateTime limiteCooldown = LocalDateTime.now().minusDays(properties.cooldownRecriacaoDias());

        return recomendacaoRepository.findByIdUsuarioAndRegra(idUsuario, achado.regra().name())
                .stream()
                .filter(r -> mesmaEntidade(r, achado))
                .anyMatch(r -> switch (r.getStatus()) {
                    case PENDENTE, ACEITA, IGNORADA -> true;
                    case CONCLUIDA -> r.getDataCriacao() != null
                            && r.getDataCriacao().isAfter(limiteCooldown);
                });
    }

    /**
     * A mesma regra pode valer para entidades diferentes ("delivery estourou" e
     * "transporte estourou" são recomendações distintas), então a comparação
     * inclui a entidade de origem.
     */
    private boolean mesmaEntidade(Recomendacao recomendacao, AchadoDraft achado) {
        return recomendacao.getEntidades() == null || recomendacao.getEntidades().isEmpty()
                ? achado.entidadeOrigemId() == null
                : recomendacao.getEntidades().stream().anyMatch(e ->
                        java.util.Objects.equals(e.getIdEntidade(), achado.entidadeOrigemId()));
    }

    private Recomendacao criar(Long idUsuario, Analise analise, AchadoDraft achado) {
        RecommendationTextService.Texto texto = textoService.gerar(achado.regra(), achado.dados());

        Recomendacao recomendacao = recomendacaoRepository.save(Recomendacao.builder()
                .idUsuario(idUsuario)
                .analise(analise)
                .regra(achado.regra().name())
                .tipo(achado.regra().tipoRecomendacao())
                .titulo(texto.titulo())
                .descricao(texto.descricao())
                .dificuldade(achado.regra().dificuldade())
                .prioridade(prioridade(achado.gravidade()))
                .status(StatusRecomendacao.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .build());

        if (achado.entidadeOrigemId() != null) {
            recomendacaoEntidadeRepository.save(RecomendacaoEntidade.builder()
                    .recomendacao(recomendacao)
                    .tipoEntidade(achado.entidadeOrigemTipo())
                    .idEntidade(achado.entidadeOrigemId())
                    .papelEntidade("ORIGEM")
                    .build());
        }

        log.info("Recomendação criada para o usuário {}: {}", idUsuario, achado.regra());
        return recomendacao;
    }

    /** A gravidade do achado vira a prioridade com que o app o mostra. */
    private Prioridade prioridade(NivelRisco gravidade) {
        if (gravidade == null) {
            return Prioridade.MEDIA;
        }
        return switch (gravidade) {
            case CRITICO -> Prioridade.CRITICA;
            case ALTO -> Prioridade.ALTA;
            case MEDIO -> Prioridade.MEDIA;
            case BAIXO -> Prioridade.BAIXA;
        };
    }
}
