-- ================================================================
-- TRANSAÇÕES LANÇADAS MANUALMENTE
--
-- Até aqui uma transação só chegava ao usuário através da conta
-- bancária (tb_transacoes.id_conta_bancaria -> tb_conta_bancaria.
-- id_usuario). Como o app agora deixa a pessoa lançar um gasto na
-- mão — quase sempre em dinheiro, sem conta conectada — a transação
-- passa a ser dona de si mesma: id_usuario direto na tabela e conta
-- bancária opcional.
-- ================================================================

ALTER TABLE tb_transacoes
    ADD COLUMN id_usuario BIGINT;

-- Backfill: quem já existe herda o dono da própria conta.
UPDATE tb_transacoes t
SET id_usuario = c.id_usuario
FROM tb_conta_bancaria c
WHERE c.id = t.id_conta_bancaria;

ALTER TABLE tb_transacoes
    ALTER COLUMN id_usuario SET NOT NULL;

ALTER TABLE tb_transacoes
    ALTER COLUMN id_conta_bancaria DROP NOT NULL;

-- A listagem do app é sempre "as transações do usuário, mais recentes
-- primeiro"; a análise lê a mesma coisa recortada por período.
CREATE INDEX idx_transacoes_usuario_data
    ON tb_transacoes (id_usuario, data_transacao DESC);


-- ================================================================
-- VENDEDORES: CATÁLOGO GLOBAL
--
-- Vendedor é uma identidade compartilhada, não uma linha por usuário:
-- a Amazon é a mesma Amazon para todo mundo. É isso que permite somar
-- as compras de um usuário num estabelecimento e compará-las com as
-- dos demais, e é o formato que o Open Finance vai devolver — lá o
-- estabelecimento vem identificado por CNPJ, já global por natureza.
--
-- O que faltava era a chave de deduplicação. cpf_cnpj é UNIQUE mas o
-- Postgres aceita vários NULLs, e hoje ninguém digita CNPJ: sem uma
-- chave por nome, cada usuário que escrevesse "Amazon" criaria mais
-- uma linha. nome_normalizado (sem acento/caixa) vira essa chave até
-- a ingestão via Open Finance trazer o CNPJ.
--
-- criado_por_usuario é só procedência — quem digitou o nome primeiro.
-- NULL = veio semeado pelo sistema. Não dá posse: qualquer usuário
-- reaproveita a mesma linha.
-- ================================================================

ALTER TABLE tb_vendedores
    ADD COLUMN criado_por_usuario BIGINT,
    ADD COLUMN nome_normalizado   VARCHAR(255);

-- Backfill do que já existe: mesma normalização aplicada no Java
-- (sem acento, sem espaço duplicado, minúsculo). TRANSLATE em vez de
-- unaccent() porque a extensão não está instalada e unaccent não é
-- IMMUTABLE — não poderia entrar num índice depois.
UPDATE tb_vendedores
SET nome_normalizado = LOWER(TRIM(REGEXP_REPLACE(
        TRANSLATE(nome_empresa,
                  'àáâãäèéêëìíîïòóôõöùúûüçÀÁÂÃÄÈÉÊËÌÍÎÏÒÓÔÕÖÙÚÛÜÇ',
                  'aaaaaeeeeiiiiooooouuuucAAAAAEEEEIIIIOOOOOUUUUC'),
        '\s+', ' ', 'g')))
WHERE nome_empresa IS NOT NULL;

-- Um estabelecimento, uma linha — para toda a base.
CREATE UNIQUE INDEX idx_vendedores_nome_normalizado
    ON tb_vendedores (nome_normalizado)
    WHERE nome_normalizado IS NOT NULL;
