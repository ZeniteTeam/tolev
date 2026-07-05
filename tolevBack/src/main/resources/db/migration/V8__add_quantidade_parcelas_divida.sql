-- ================================================================
-- DIVIDAS
-- Número total de parcelas da dívida, informado na criação.
-- ================================================================

ALTER TABLE tb_dividas
    ADD COLUMN quantidade_parcelas INTEGER;
