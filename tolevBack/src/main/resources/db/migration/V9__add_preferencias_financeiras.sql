-- ================================================================
-- USERS DOMAIN - Preferências financeiras
-- Estratégia de quitação de dívidas + método de orçamento escolhidos
-- pelo usuário, com campos de apoio usados por projeções, análises e
-- recomendações. Relação 1:1 com tb_usuarios.
-- ================================================================

CREATE TABLE tb_preferencias_financeiras (
    id                        BIGSERIAL     PRIMARY KEY,
    id_usuario                BIGINT        NOT NULL UNIQUE REFERENCES tb_usuarios(id),
    metodo_quitacao           VARCHAR(50)   NOT NULL DEFAULT 'AVALANCHE',
    aporte_extra_mensal       NUMERIC(15,2) DEFAULT 0,
    metodo_orcamento          VARCHAR(50)   NOT NULL DEFAULT 'REGRA_50_30_20',
    renda_mensal              NUMERIC(15,2) DEFAULT 0,
    perc_fixos                INTEGER       NOT NULL DEFAULT 50,
    perc_dividas              INTEGER       NOT NULL DEFAULT 30,
    perc_lazer                INTEGER       NOT NULL DEFAULT 20,
    reserva_emergencia_meta   NUMERIC(15,2) DEFAULT 0,
    criado_em                 TIMESTAMP,
    atualizado_em             TIMESTAMP
);

-- Cria uma linha de preferências padrão para cada usuário já existente.
INSERT INTO tb_preferencias_financeiras (id_usuario, criado_em, atualizado_em)
SELECT id, NOW(), NOW() FROM tb_usuarios;
