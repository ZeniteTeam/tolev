-- ================================================================
-- USERS DOMAIN - Perfil financeiro (onboarding)
-- Campos coletados no cadastro que direcionam análises e recomendações:
-- objetivo principal, situação financeira atual e ocupação.
-- A renda mensal é armazenada em tb_preferencias_financeiras.
-- ================================================================

ALTER TABLE tb_usuarios
    ADD COLUMN objetivo_principal   VARCHAR(50),
    ADD COLUMN situacao_financeira  VARCHAR(50),
    ADD COLUMN ocupacao             VARCHAR(50);
