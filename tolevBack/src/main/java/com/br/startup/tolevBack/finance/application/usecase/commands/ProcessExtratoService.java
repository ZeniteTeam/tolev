package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.common.gemini.GeminiException;
import com.br.startup.tolevBack.common.gemini.GeminiNaoConfiguradoException;
import com.br.startup.tolevBack.finance.application.service.ExtratoAnaliseService;
import com.br.startup.tolevBack.finance.application.service.ExtratoAnaliseService.ExtratoExtraido;
import com.br.startup.tolevBack.finance.application.service.ExtratoGravacaoService;
import com.br.startup.tolevBack.finance.exception.ExtratoSemNovidadeException;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoSistema;
import com.br.startup.tolevBack.finance.internal.entity.ImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoSistemaRepository;
import com.br.startup.tolevBack.finance.internal.repository.IImportacaoExtratoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Lê o PDF no Gemini e fecha a importação — fora da thread da requisição.
 *
 * <p>Bean separado do {@link ImportExtratoService} porque {@code @Async} só vale
 * quando a chamada passa pelo proxy do Spring: um método assíncrono invocado de
 * dentro da própria classe roda síncrono, e a importação voltaria a travar o
 * upload sem nenhum sinal de que voltou.
 *
 * <p>Aqui nada pode escapar sem virar estado: se este método terminar por
 * exceção, a importação fica presa em {@code PROCESSANDO} e o app espera um
 * resultado que nunca chega. Por isso o {@code catch (Exception)} amplo — ele
 * não esconde o problema, transforma o problema em algo que a tela sabe mostrar.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessExtratoService {

    private final ExtratoAnaliseService extratoAnalise;
    private final ExtratoGravacaoService gravacaoService;
    private final ICategoriaGastoSistemaRepository categoriaSistemaRepository;
    private final IImportacaoExtratoRepository importacaoRepository;

    @Async("extratoExecutor")
    public void execute(Long idImportacao, byte[] pdf) {
        ImportacaoExtrato importacao = importacaoRepository.findById(idImportacao).orElse(null);
        if (importacao == null) {
            // Só acontece se a importação for desfeita enquanto estava na fila.
            log.warn("Importação {} sumiu antes de ser processada.", idImportacao);
            return;
        }

        try {
            List<CategoriaGastoSistema> catalogo = categoriaSistemaRepository.findByAtivoTrueOrderByNomeAsc();
            if (catalogo.isEmpty()) {
                throw new IllegalStateException("Nenhuma categoria ativa no catálogo.");
            }

            ExtratoExtraido extrato = extratoAnalise.analisar(pdf, catalogo.stream()
                    .map(CategoriaGastoSistema::getNome)
                    .toList());

            // O aviso ao motor de análise sai de dentro da gravação, que é quem
            // tem transação — o listener escuta com AFTER_COMMIT.
            gravacaoService.gravar(idImportacao, extrato, catalogo);

        } catch (ExtratoSemNovidadeException e) {
            // Leitura correta, resultado vazio: o texto já foi escrito pensando
            // no usuário, e não há nada a registrar no log fora do fato.
            log.info("Importação {} sem novidade: {}", idImportacao, e.getMessage());
            gravacaoService.falhar(idImportacao, e.getMessage());

        } catch (GeminiNaoConfiguradoException e) {
            // ERROR, e não WARN: o servidor está mal configurado e nenhum upload
            // vai funcionar até alguém arrumar. Não mandamos conferir o PDF — o
            // arquivo do usuário não tem nada a ver com isso, e procurar defeito
            // nele é justamente o tempo que a mensagem errada faz perder.
            log.error("GEMINI_API_KEY ausente: importação {} recusada sem sair do servidor.",
                    idImportacao, e);
            gravacaoService.falhar(idImportacao,
                    "A leitura de extratos está indisponível no momento. Não é nada com o seu arquivo — tente mais tarde.");

        } catch (GeminiException e) {
            log.warn("Gemini falhou ao ler a importação {}", idImportacao, e);
            gravacaoService.falhar(idImportacao,
                    "Não conseguimos ler esse extrato. Confira se o PDF abre normalmente e tente de novo.");

        } catch (Exception e) {
            log.error("Falha inesperada ao processar a importação {}", idImportacao, e);
            gravacaoService.falhar(idImportacao,
                    "Algo deu errado ao importar seu extrato. Tente enviar de novo em alguns minutos.");
        }
    }
}
