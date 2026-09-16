package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.response.ExtratoImportacaoResponse;
import com.br.startup.tolevBack.finance.application.service.ExtratoGravacaoService;
import com.br.startup.tolevBack.finance.internal.entity.Banco;
import com.br.startup.tolevBack.finance.internal.entity.ImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.enums.StatusImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.mapper.ImportacaoExtratoMapper;
import com.br.startup.tolevBack.finance.internal.repository.IBankRepository;
import com.br.startup.tolevBack.finance.internal.repository.IImportacaoExtratoRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Aceita o PDF do extrato e devolve na hora, sem esperar a leitura.
 *
 * <p>Ler um extrato mensal no Gemini leva dezenas de segundos. Segurar a
 * requisição por todo esse tempo prenderia o usuário numa tela de espera e
 * perderia o trabalho já pago à API a qualquer oscilação de rede. Então este
 * serviço só faz o mínimo que precisa ser síncrono — validar e registrar — e
 * entrega o resto para {@link ProcessExtratoService}, que roda em outra thread.
 *
 * <p><strong>Um extrato por vez, por usuário.</strong> Não é limitação de
 * capacidade: duas importações simultâneas do mesmo banco leriam o mesmo marco e
 * as duas se achariam a primeira, duplicando o período em comum. Serializar por
 * usuário elimina a corrida e, de quebra, deixa a tela de acompanhamento com um
 * caso só para mostrar.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExtratoService {

    /** Assinatura de arquivo PDF: os quatro primeiros bytes são sempre "%PDF". */
    private static final byte[] ASSINATURA_PDF = {0x25, 0x50, 0x44, 0x46};

    private final IBankRepository bankRepository;
    private final IImportacaoExtratoRepository importacaoRepository;
    private final ProcessExtratoService processExtrato;
    private final ExtratoGravacaoService gravacaoService;

    public ExtratoImportacaoResponse execute(Long idUsuario, Long idBanco, String nomeArquivo, byte[] pdf) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("Informe o usuário do extrato.");
        }
        if (idBanco == null) {
            throw new IllegalArgumentException("Informe de qual banco é o extrato.");
        }
        exigirPdf(pdf);

        if (importacaoRepository.existsByIdUsuarioAndStatus(idUsuario, StatusImportacaoExtrato.PROCESSANDO)) {
            throw new IllegalArgumentException(
                    "Você já tem um extrato sendo lido. Espere ele terminar para enviar o próximo.");
        }

        Banco banco = bankRepository.findById(idBanco)
                .orElseThrow(() -> new NotFoundException("Banco não encontrado com id: " + idBanco));

        // Sem @Transactional de propósito: o save precisa estar comitado antes de
        // a outra thread começar, senão ela procura uma importação que ainda não
        // existe para ninguém além desta transação.
        ImportacaoExtrato importacao = importacaoRepository.save(ImportacaoExtrato.builder()
                .idUsuario(idUsuario)
                .banco(banco)
                .status(StatusImportacaoExtrato.PROCESSANDO)
                .nomeArquivo(nomeArquivo)
                .criadoEm(LocalDateTime.now())
                .build());

        try {
            processExtrato.execute(importacao.getId(), pdf);
        } catch (TaskRejectedException e) {
            // Fila cheia. Deixar a linha em PROCESSANDO seria pior do que a
            // falha: ninguém iria processá-la, e o app ficaria esperando para
            // sempre um resultado que não vem.
            log.warn("Fila de importação de extrato cheia; recusando importação {}", importacao.getId(), e);
            gravacaoService.falhar(importacao.getId(),
                    "Estamos com muitos extratos na fila agora. Tente de novo em alguns minutos.");
            return ImportacaoExtratoMapper.toResponse(
                    importacaoRepository.findById(importacao.getId()).orElseThrow());
        }

        return ImportacaoExtratoMapper.toResponse(importacao);
    }

    /**
     * Confere a assinatura em vez do content-type: o cabeçalho do multipart é
     * escolhido pelo cliente e um {@code .pdf} renomeado passaria por ele, só
     * para o Gemini receber lixo e cobrar por isso.
     */
    private void exigirPdf(byte[] pdf) {
        if (pdf == null || pdf.length < ASSINATURA_PDF.length) {
            throw new IllegalArgumentException("Envie o PDF do extrato.");
        }
        for (int i = 0; i < ASSINATURA_PDF.length; i++) {
            if (pdf[i] != ASSINATURA_PDF[i]) {
                throw new IllegalArgumentException(
                        "Por enquanto só aceitamos extrato em PDF. Baixe o arquivo em PDF no app do seu banco.");
            }
        }
    }
}
