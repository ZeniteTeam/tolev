package com.br.startup.tolevBack.finance.internal.repository;

import com.br.startup.tolevBack.finance.internal.entity.TransacaoRecorrente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ITransacaoRecorrenteRepository extends JpaRepository<TransacaoRecorrente, Long> {

    /**
     * Os gastos fixos vivos do usuário, na ordem em que caem no mês. Dia nulo
     * vai para o fim — é o que o Postgres faz por padrão com NULLS LAST em
     * ordenação ascendente, e é onde ele incomoda menos.
     */
    List<TransacaoRecorrente> findByIdUsuarioAndAtivoTrueOrderByDiaRecorrenciaAscIdAsc(Long idUsuario);

    /** O dono entra na busca: id sozinho deixaria editar gasto dos outros. */
    Optional<TransacaoRecorrente> findByIdAndIdUsuario(Long id, Long idUsuario);

    /**
     * Carimba todos os gastos vivos de uma vez — é o "está tudo certo" do mês,
     * um gesto só, não um por linha.
     */
    @Modifying
    @Query("""
            UPDATE TransacaoRecorrente t
               SET t.confirmadoEm = :data
             WHERE t.idUsuario = :idUsuario
               AND t.ativo = true
            """)
    int confirmarTodos(@Param("idUsuario") Long idUsuario, @Param("data") LocalDate data);
}
