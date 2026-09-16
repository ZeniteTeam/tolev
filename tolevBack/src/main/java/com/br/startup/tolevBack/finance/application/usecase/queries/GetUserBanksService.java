package com.br.startup.tolevBack.finance.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.BancoUsuarioResponse;
import com.br.startup.tolevBack.finance.internal.entity.ImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.enums.StatusImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.repository.BancoUsuarioProjection;
import com.br.startup.tolevBack.finance.internal.repository.IImportacaoExtratoRepository;
import com.br.startup.tolevBack.finance.internal.repository.ITransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Os bancos que este usuário de fato usa, montados a partir do que já entrou.
 *
 * <p>Não confundir com {@code GetBanksService}, que devolve o catálogo — a lista
 * de onde escolher na hora de subir o extrato. Aqui só aparece banco com
 * transação importada, e cada linha responde "o que eu já trouxe deste banco, e
 * até quando".
 *
 * <p>As contagens saem das transações, não de contadores guardados na
 * importação: desfazer um upload apaga as transações dele, e um contador
 * separado precisaria ser decrementado na mão para não mentir depois disso.
 */
@Service
@RequiredArgsConstructor
public class GetUserBanksService {

    private final ITransactionRepository transactionRepository;
    private final IImportacaoExtratoRepository importacaoRepository;

    @Transactional(readOnly = true)
    public List<BancoUsuarioResponse> execute(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("Informe o usuário.");
        }

        List<ImportacaoExtrato> concluidas = importacaoRepository
                .findByIdUsuarioOrderByCriadoEmDesc(idUsuario).stream()
                .filter(i -> i.getStatus() == StatusImportacaoExtrato.CONCLUIDA)
                .toList();

        // O marco de cada banco: até onde ele está coberto. É o mesmo número que
        // decide o que entra no próximo upload, então mostrá-lo aqui explica de
        // antemão por que parte do extrato seguinte será pulada.
        Map<Long, LocalDate> marcoPorBanco = concluidas.stream()
                .collect(Collectors.toMap(
                        i -> i.getBanco().getId(),
                        ImportacaoExtrato::getDataFim,
                        (a, b) -> a.isAfter(b) ? a : b));

        // A lista já vem ordenada por data decrescente, então o primeiro de cada
        // banco é o upload mais recente dele.
        Map<Long, LocalDateTime> ultimoImportPorBanco = concluidas.stream()
                .collect(Collectors.toMap(
                        i -> i.getBanco().getId(),
                        ImportacaoExtrato::getCriadoEm,
                        (primeiro, seguinte) -> primeiro));

        return transactionRepository.resumoPorBanco(idUsuario).stream()
                .map(p -> paraResponse(p, marcoPorBanco, ultimoImportPorBanco))
                .toList();
    }

    private BancoUsuarioResponse paraResponse(
            BancoUsuarioProjection p,
            Map<Long, LocalDate> marcoPorBanco,
            Map<Long, LocalDateTime> ultimoImportPorBanco) {
        return new BancoUsuarioResponse(
                p.getIdBanco(),
                p.getNomeBanco(),
                p.getCodigoBanco(),
                p.getQuantidadeTransacoes(),
                p.getTotalEntradas(),
                p.getTotalSaidas(),
                p.getPrimeiraTransacao(),
                p.getUltimaTransacao(),
                marcoPorBanco.get(p.getIdBanco()),
                ultimoImportPorBanco.get(p.getIdBanco()));
    }
}
