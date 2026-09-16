package com.br.startup.tolevBack.finance.internal.repository;

import com.br.startup.tolevBack.finance.internal.entity.ImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.enums.StatusImportacaoExtrato;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IImportacaoExtratoRepository extends JpaRepository<ImportacaoExtrato, Long> {

    /**
     * O marco daquele usuário naquele banco: a data final da importação
     * concluída mais recente. Vazio quando é o primeiro extrato desse banco.
     *
     * <p>O filtro por status não é detalhe: uma importação em andamento ainda
     * não tem {@code dataFim}, e uma que falhou não gravou transação nenhuma —
     * qualquer uma das duas como marco bloquearia um período que nunca entrou.
     */
    Optional<ImportacaoExtrato> findTopByIdUsuarioAndBancoIdAndStatusOrderByDataFimDesc(
            Long idUsuario, Long idBanco, StatusImportacaoExtrato status);

    /**
     * O histórico da tela de importação. O banco vem junto porque cada linha da
     * lista mostra o nome dele.
     */
    @EntityGraph(attributePaths = "banco")
    List<ImportacaoExtrato> findByIdUsuarioOrderByCriadoEmDesc(Long idUsuario);

    /** Só um extrato por vez, por usuário — ver {@code ImportExtratoService}. */
    boolean existsByIdUsuarioAndStatus(Long idUsuario, StatusImportacaoExtrato status);
}
