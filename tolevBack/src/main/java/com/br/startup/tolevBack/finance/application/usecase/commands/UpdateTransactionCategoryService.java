package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.request.TransactionCategoryRequest;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoSistema;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoUsuario;
import com.br.startup.tolevBack.finance.internal.entity.Transacao;
import com.br.startup.tolevBack.finance.internal.enums.TipoCategoriaGasto;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;
import com.br.startup.tolevBack.finance.internal.mapper.TransactionMapper;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoSistemaRepository;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoUsuarioRepository;
import com.br.startup.tolevBack.finance.internal.repository.ITransactionRepository;
import com.br.startup.tolevBack.shared.events.DadosFinanceirosAlteradosEvent;
import com.br.startup.tolevBack.shared.events.OrigemAlteracao;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classifica (ou reclassifica) uma transação já gravada.
 *
 * <p>É o que faz a tela de "classificação de gastos" valer alguma coisa: quase
 * tudo que entra por extrato importado chega sem categoria, e sem isso a única
 * saída seria apagar a transação e digitar de novo.
 */
@Service
@RequiredArgsConstructor
public class UpdateTransactionCategoryService {

    private final ITransactionRepository transactionRepository;
    private final ICategoriaGastoSistemaRepository categoriaSistemaRepository;
    private final ICategoriaGastoUsuarioRepository categoriaUsuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TransactionResponse execute(Long id, TransactionCategoryRequest request) {
        Transacao transacao = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transação não encontrada com id: " + id));

        Long idSistema = request.idCategoriaGastoSistema();
        Long idUsuarioCategoria = request.idCategoriaGastoUsuario();
        if (idSistema != null && idUsuarioCategoria != null) {
            throw new IllegalArgumentException("Escolha apenas uma categoria para a transação.");
        }

        transacao.setCategoriaGastoSistema(resolveSistema(idSistema, transacao));
        transacao.setCategoriaGastoUsuario(resolveUsuario(idUsuarioCategoria, transacao));

        Transacao salva = transactionRepository.save(transacao);

        // A categoria é entrada da análise por categoria: classificar move os
        // números da tela tanto quanto lançar uma transação nova.
        eventPublisher.publishEvent(DadosFinanceirosAlteradosEvent.de(
                salva.getIdUsuario(), OrigemAlteracao.TRANSACAO_CATEGORIZADA, "TRANSACAO", salva.getId()));

        return TransactionMapper.toResponse(salva);
    }

    private CategoriaGastoSistema resolveSistema(Long idCategoria, Transacao transacao) {
        if (idCategoria == null) {
            return null;
        }
        CategoriaGastoSistema categoria = categoriaSistemaRepository.findById(idCategoria)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada com id: " + idCategoria));
        validarTipo(categoria.getTipo(), transacao.getTipo());
        return categoria;
    }

    private CategoriaGastoUsuario resolveUsuario(Long idCategoria, Transacao transacao) {
        if (idCategoria == null) {
            return null;
        }
        CategoriaGastoUsuario categoria = categoriaUsuarioRepository.findById(idCategoria)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada com id: " + idCategoria));
        if (!categoria.getIdUsuario().equals(transacao.getIdUsuario())) {
            throw new IllegalArgumentException("Essa categoria não pertence ao dono da transação.");
        }
        validarTipo(categoria.getTipo(), transacao.getTipo());
        return categoria;
    }

    /** Mesma regra da criação: categoria de despesa não classifica receita. */
    private void validarTipo(TipoCategoriaGasto tipoCategoria, TipoTransacao tipoTransacao) {
        boolean combina = (tipoTransacao == TipoTransacao.RECEITA && tipoCategoria == TipoCategoriaGasto.RECEITA)
                || (tipoTransacao == TipoTransacao.DESPESA && tipoCategoria == TipoCategoriaGasto.DESPESA);
        if (!combina) {
            throw new IllegalArgumentException("A categoria escolhida não vale para esse tipo de transação.");
        }
    }
}
