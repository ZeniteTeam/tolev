-- ================================================================
-- GASTOS FIXOS DECLARADOS PELO USUÁRIO
--
-- Reaproveita tb_transacoes_recorrentes em vez de criar uma segunda
-- tabela de despesa recorrente. A tabela já tinha valor, descrição,
-- dia_recorrencia, data_inicio/fim e ativo (V1 + V3); faltava só poder
-- existir sem conta bancária e saber de quem é.
--
-- O que o usuário declara e o que for detectado no extrato mais tarde
-- são a mesma coisa vista de dois lados — separá-los em duas tabelas
-- garantiria que um dia divergissem.
-- ================================================================

ALTER TABLE tb_transacoes_recorrentes
    ADD COLUMN id_usuario    BIGINT,
    -- Última vez que a pessoa disse "este valor continua valendo". Null =
    -- nunca confirmado, que é o estado de tudo que acabou de ser cadastrado.
    ADD COLUMN confirmado_em DATE;

-- As linhas que já existiam vieram de conta bancária, então o dono sai dela.
UPDATE tb_transacoes_recorrentes r
SET id_usuario = c.id_usuario
FROM tb_conta_bancaria c
WHERE r.id_conta_bancaria = c.id
  AND r.id_usuario IS NULL;

-- Gasto fixo digitado à mão não sai de conta nenhuma: aluguel pago em
-- dinheiro é tão recorrente quanto o débito automático.
ALTER TABLE tb_transacoes_recorrentes
    ALTER COLUMN id_conta_bancaria DROP NOT NULL;

CREATE INDEX idx_transacoes_recorrentes_usuario
    ON tb_transacoes_recorrentes (id_usuario, ativo);
