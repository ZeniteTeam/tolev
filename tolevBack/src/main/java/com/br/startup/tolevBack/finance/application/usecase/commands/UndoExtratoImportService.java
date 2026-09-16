package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.internal.entity.ImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.entity.Transacao;
import com.br.startup.tolevBack.finance.internal.enums.StatusImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.repository.IImportacaoExtratoRepository;
import com.br.startup.tolevBack.finance.internal.repository.ITransactionRepository;
import com.br.startup.tolevBack.shared.events.DadosFinanceirosAlteradosEvent;
import com.br.startup.tolevBack.shared.events.OrigemAlteracao;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Desfaz uma importação de extrato inteira.
 *
 * <p>A rede de segurança de gravar sem tela de revisão: quando o Gemini lê um
 * PDF errado, o usuário apaga o upload de uma vez em vez de caçar trinta
 * transações à mão — o que nem seria possível, já que não existe
 * {@code DELETE /transactions}.
 *
 * <p>Apagar o registro da importação também devolve o marco daquele banco à
 * importação anterior, então o mesmo período pode ser subido de novo. O marco
 * continua sendo sempre o {@code dataFim} mais recente que sobrou: desfazer uma
 * importação antiga quando já existe uma mais nova não reabre aquele período —
 * a mais nova segue mandando.
 *
 * <p>Serve também para dispensar uma importação que falhou: nada a apagar, mas
 * o registro sai do histórico e o aviso de erro some da tela.
 *
 * <p>Nenhum saldo é mexido porque nenhum saldo foi mexido na importação.
 */
@Service
@RequiredArgsConstructor
public class UndoExtratoImportService {

    private final ITransactionRepository transactionRepository;
    private final IImportacaoExtratoRepository importacaoRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void execute(Long idUsuario, Long idImportacao) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("Informe o usuário da importação.");
        }
        ImportacaoExtrato importacao = importacaoRepository.findById(idImportacao)
                .orElseThrow(() -> new NotFoundException(
                        "Importação não encontrada com id: " + idImportacao));

        if (!idUsuario.equals(importacao.getIdUsuario())) {
            throw new IllegalArgumentException("Essa importação não pertence ao usuário informado.");
        }

        // Enquanto o Gemini está lendo, apagar deixaria a outra thread gravando
        // transações que apontam para uma importação que não existe mais.
        if (importacao.getStatus() == StatusImportacaoExtrato.PROCESSANDO) {
            throw new IllegalArgumentException(
                    "Esse extrato ainda está sendo lido. Espere terminar para poder desfazer.");
        }

        List<Transacao> transacoes = transactionRepository.findByIdImportacaoExtrato(idImportacao);
        transactionRepository.deleteAll(transacoes);
        importacaoRepository.delete(importacao);

        // Nada mudou no retrato financeiro quando não havia nada gravado — uma
        // importação que falhou some sem custar um recálculo à toa.
        if (!transacoes.isEmpty()) {
            // Some um mês de gastos de uma vez: mesmo impacto da importação, e
            // pelo mesmo caminho.
            eventPublisher.publishEvent(DadosFinanceirosAlteradosEvent.de(
                    idUsuario, OrigemAlteracao.EXTRATO_IMPORTADO, "IMPORTACAO_EXTRATO", idImportacao));
        }
    }
}
