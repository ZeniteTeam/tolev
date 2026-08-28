package com.br.startup.tolevBack.finance.application.service;

import com.br.startup.tolevBack.finance.internal.entity.Vendedor;
import com.br.startup.tolevBack.finance.internal.repository.IVendorRepository;
import com.br.startup.tolevBack.finance.internal.util.TextNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Encontra (ou cria) o estabelecimento a partir do texto que o usuário digitou.
 *
 * <p>O catálogo é global: quem escreve "Amazon" cai na mesma linha que todos os
 * outros usuários, e é isso que deixa somar os gastos de uma pessoa naquele
 * lugar e compará-los com os da base inteira.
 *
 * <p>Roda em transação própria ({@code REQUIRES_NEW}) de propósito. Sendo
 * compartilhada, a tabela recebe inserções de usuários diferentes ao mesmo
 * tempo, e duas pessoas registrando a primeira compra na Amazon no mesmo
 * instante passam as duas pela busca antes de qualquer uma inserir. A segunda
 * esbarra no índice único — isolar a transação faz esse erro derrubar só a
 * resolução do vendedor, deixando a transação financeira do usuário intacta
 * para o chamador tentar de novo.
 *
 * <p>A retentativa fica no chamador de propósito: chamada interna não passa
 * pelo proxy do Spring, então um retry aqui dentro rodaria na transação do
 * chamador e perderia justamente o isolamento que motiva este serviço.
 */
@Service
@RequiredArgsConstructor
public class VendorResolver {

    private final IVendorRepository vendorRepository;

    /**
     * @param nomeVendedor     texto livre; {@code null}/em branco devolve {@code null}
     * @param idUsuarioCriador gravado apenas como procedência, não dá posse
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Vendedor resolve(String nomeVendedor, Long idUsuarioCriador) {
        String normalizado = TextNormalizer.normalize(nomeVendedor);
        if (normalizado == null) {
            return null;
        }

        return vendorRepository.findByNomeNormalizado(normalizado)
                .orElseGet(() -> vendorRepository.saveAndFlush(Vendedor.builder()
                        .criadoPorUsuario(idUsuarioCriador)
                        .nomeEmpresa(nomeVendedor.trim())
                        .nomeNormalizado(normalizado)
                        .build()));
    }
}
