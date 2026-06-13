-- ================================================================
-- DIVIDAS
-- Colunas presentes na entidade Divida desde antes de V1, mas
-- nunca refletidas na tabela.
-- ================================================================

ALTER TABLE tb_dividas
    ADD COLUMN saldo_atual NUMERIC(19, 2),
    ADD COLUMN taxa_juros  NUMERIC(19, 2);
