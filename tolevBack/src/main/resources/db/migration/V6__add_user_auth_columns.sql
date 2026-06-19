-- ================================================================
-- USERS DOMAIN - Autenticação
-- Colunas de apoio à autenticação/autorização e auditoria do usuário.
-- ================================================================

ALTER TABLE tb_usuarios
    ADD COLUMN papel         VARCHAR(50) NOT NULL DEFAULT 'USER',
    ADD COLUMN ativo         BOOLEAN     NOT NULL DEFAULT TRUE,
    ADD COLUMN criado_em     TIMESTAMP,
    ADD COLUMN atualizado_em TIMESTAMP;
