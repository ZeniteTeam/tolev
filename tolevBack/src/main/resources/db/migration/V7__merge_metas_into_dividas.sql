-- ================================================================
-- PROGRESSION DOMAIN
-- Metas foram descontinuadas: toda meta agora é uma dívida.
-- As dívidas ganham os campos que o app usa (banco, tipo, parcela
-- mínima e peso emocional) e as tabelas de meta são removidas.
-- ================================================================

-- Novos campos da dívida, alinhados ao contrato do front-end
ALTER TABLE tb_dividas
    ADD COLUMN banco          VARCHAR(255),
    ADD COLUMN tipo           VARCHAR(50),
    ADD COLUMN parcela_minima NUMERIC(19, 2),
    ADD COLUMN peso_emocional INTEGER;

-- Remoção das tabelas de meta (progresso primeiro por causa da FK)
DROP TABLE IF EXISTS tb_progresso_meta;
DROP TABLE IF EXISTS tb_metas;
