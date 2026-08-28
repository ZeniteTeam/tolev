package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.request.TransactionRequest;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.application.service.VendorResolver;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoSistema;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoUsuario;
import com.br.startup.tolevBack.finance.internal.entity.ContaBancaria;
import com.br.startup.tolevBack.finance.internal.entity.Transacao;
import com.br.startup.tolevBack.finance.internal.entity.Vendedor;
import com.br.startup.tolevBack.finance.internal.enums.TipoCategoriaGasto;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;
import com.br.startup.tolevBack.finance.internal.mapper.TransactionMapper;
import com.br.startup.tolevBack.finance.internal.repository.IAccountRepository;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoSistemaRepository;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoUsuarioRepository;
import com.br.startup.tolevBack.finance.internal.repository.ITransactionRepository;
import com.br.startup.tolevBack.finance.internal.util.TextNormalizer;
import com.br.startup.tolevBack.shared.events.DadosFinanceirosAlteradosEvent;
import com.br.startup.tolevBack.shared.events.OrigemAlteracao;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Cria uma transação lançada à mão pelo usuário.
 *
 * <p>Conta bancária é opcional — a maioria desses lançamentos é dinheiro vivo,
 * sem conta conectada. Quando existe, o saldo dela é movido junto, senão a tela
 * de saldo passaria a discordar da lista de transações.
 */
@Service
@RequiredArgsConstructor
public class CreateTransactionService {

    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final VendorResolver vendorResolver;
    private final ICategoriaGastoSistemaRepository categoriaSistemaRepository;
    private final ICategoriaGastoUsuarioRepository categoriaUsuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TransactionResponse execute(TransactionRequest request) {
        Long idUsuario = request.idUsuario();
        if (idUsuario == null) {
            throw new IllegalArgumentException("Informe o usuário da transação.");
        }
        if (request.valor() == null || request.valor().signum() <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero.");
        }
        if (request.tipo() == null) {
            throw new IllegalArgumentException("Informe se a transação é uma receita ou uma despesa.");
        }
        if (request.tipo() == TipoTransacao.TRANSFERENCIA) {
            throw new IllegalArgumentException(
                    "Transferências ainda não podem ser lançadas manualmente.");
        }

        LocalDate dataTransacao = request.dataTransacao() != null ? request.dataTransacao() : LocalDate.now();
        if (dataTransacao.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data da transação não pode estar no futuro.");
        }

        ContaBancaria conta = resolveConta(request.idContaBancaria(), idUsuario);
        Vendedor vendedor = resolveVendedor(request.nomeVendedor(), idUsuario);
        boolean parcelado = Boolean.TRUE.equals(request.parcelado());

        Transacao transacao = Transacao.builder()
                .idUsuario(idUsuario)
                .contaBancaria(conta)
                .vendedor(vendedor)
                .valor(request.valor())
                .dataTransacao(dataTransacao)
                .tipo(request.tipo())
                .descricao(request.descricao())
                .descricaoNormalizada(TextNormalizer.normalize(request.descricao()))
                .parcelado(parcelado)
                .totalParcelas(parcelado ? request.totalParcelas() : null)
                .numeroParcela(parcelado ? request.numeroParcela() : null)
                .metodoPagamento(request.metodoPagamento())
                .categoriaGastoSistema(resolveCategoriaSistema(request))
                .categoriaGastoUsuario(resolveCategoriaUsuario(request, idUsuario))
                .build();

        if (parcelado) {
            validarParcelamento(transacao);
        }
        if (conta != null) {
            aplicarNoSaldo(conta, request.tipo(), request.valor());
        }

        Transacao salva = transactionRepository.save(transacao);

        // Publicado aqui, entregue só depois do commit: o módulo de análise
        // decide sozinho o que recalcular a partir disso.
        eventPublisher.publishEvent(DadosFinanceirosAlteradosEvent.de(
                idUsuario, OrigemAlteracao.TRANSACAO_CRIADA, "TRANSACAO", salva.getId()));

        return TransactionMapper.toResponse(salva);
    }

    /** A conta é opcional, mas se vier tem que ser do próprio usuário. */
    private ContaBancaria resolveConta(Long idConta, Long idUsuario) {
        if (idConta == null) {
            return null;
        }
        ContaBancaria conta = accountRepository.findById(idConta)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada com id: " + idConta));
        if (!idUsuario.equals(conta.getIdUsuario())) {
            throw new IllegalArgumentException("Essa conta não pertence ao usuário informado.");
        }
        return conta;
    }

    /**
     * O app manda o estabelecimento como texto livre e o {@link VendorResolver}
     * o transforma numa linha do catálogo global.
     *
     * <p>O retry vive aqui, e não dentro do resolver, porque só a chamada
     * através do bean injetado passa pelo proxy que abre a transação separada.
     * Se duas pessoas estrearam na Amazon ao mesmo tempo, a que perdeu a corrida
     * repete a busca e encontra a linha recém-criada pela outra.
     */
    private Vendedor resolveVendedor(String nomeVendedor, Long idUsuario) {
        try {
            return vendorResolver.resolve(nomeVendedor, idUsuario);
        } catch (DataIntegrityViolationException colisao) {
            return vendorResolver.resolve(nomeVendedor, idUsuario);
        }
    }

    private CategoriaGastoSistema resolveCategoriaSistema(TransactionRequest request) {
        if (request.idCategoriaGastoSistema() == null) {
            return null;
        }
        if (request.idCategoriaGastoUsuario() != null) {
            throw new IllegalArgumentException("Escolha apenas uma categoria para a transação.");
        }
        CategoriaGastoSistema categoria = categoriaSistemaRepository.findById(request.idCategoriaGastoSistema())
                .orElseThrow(() -> new NotFoundException(
                        "Categoria não encontrada com id: " + request.idCategoriaGastoSistema()));
        validarTipoCategoria(categoria.getTipo(), request.tipo());
        return categoria;
    }

    private CategoriaGastoUsuario resolveCategoriaUsuario(TransactionRequest request, Long idUsuario) {
        if (request.idCategoriaGastoUsuario() == null) {
            return null;
        }
        CategoriaGastoUsuario categoria = categoriaUsuarioRepository.findById(request.idCategoriaGastoUsuario())
                .orElseThrow(() -> new NotFoundException(
                        "Categoria não encontrada com id: " + request.idCategoriaGastoUsuario()));
        if (!idUsuario.equals(categoria.getIdUsuario())) {
            throw new IllegalArgumentException("Essa categoria não pertence ao usuário informado.");
        }
        validarTipoCategoria(categoria.getTipo(), request.tipo());
        return categoria;
    }

    /** Categoria de despesa em receita (e vice-versa) quebraria toda a análise. */
    private void validarTipoCategoria(TipoCategoriaGasto tipoCategoria, TipoTransacao tipoTransacao) {
        boolean combina = (tipoTransacao == TipoTransacao.RECEITA && tipoCategoria == TipoCategoriaGasto.RECEITA)
                || (tipoTransacao == TipoTransacao.DESPESA && tipoCategoria == TipoCategoriaGasto.DESPESA);
        if (!combina) {
            throw new IllegalArgumentException("A categoria escolhida não vale para esse tipo de transação.");
        }
    }

    private void validarParcelamento(Transacao transacao) {
        BigDecimal total = transacao.getTotalParcelas();
        BigDecimal numero = transacao.getNumeroParcela();
        if (total == null || total.compareTo(BigDecimal.ONE) <= 0) {
            throw new IllegalArgumentException("Uma compra parcelada precisa de pelo menos 2 parcelas.");
        }
        if (numero == null || numero.compareTo(BigDecimal.ONE) < 0 || numero.compareTo(total) > 0) {
            throw new IllegalArgumentException("O número da parcela precisa estar entre 1 e o total de parcelas.");
        }
    }

    private void aplicarNoSaldo(ContaBancaria conta, TipoTransacao tipo, BigDecimal valor) {
        BigDecimal delta = tipo == TipoTransacao.RECEITA ? valor : valor.negate();
        conta.setSaldoAtual(somar(conta.getSaldoAtual(), delta));
        conta.setSaldoDisponivel(somar(conta.getSaldoDisponivel(), delta));
        conta.setUltimaAtualizacao(LocalDateTime.now());
        conta.setAtualizadoEm(LocalDateTime.now());
    }

    private BigDecimal somar(BigDecimal saldo, BigDecimal delta) {
        return (saldo != null ? saldo : BigDecimal.ZERO).add(delta);
    }
}
