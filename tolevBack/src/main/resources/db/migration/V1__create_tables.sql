-- ================================================================
-- USERS DOMAIN
-- ================================================================

CREATE TABLE tb_usuarios (
    id               BIGSERIAL PRIMARY KEY,
    nome             VARCHAR(255),
    genero           VARCHAR(50),
    data_nascimento  DATE,
    nome_usuario     VARCHAR(100) NOT NULL UNIQUE,
    senha            VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE tb_assinaturas (
    id                 BIGSERIAL PRIMARY KEY,
    modelo_assinatura  VARCHAR(100) NOT NULL
);

CREATE TABLE tb_usuario_assinaturas (
    id             BIGSERIAL PRIMARY KEY,
    id_usuario     BIGINT       NOT NULL REFERENCES tb_usuarios(id),
    id_assinatura  BIGINT       NOT NULL REFERENCES tb_assinaturas(id),
    data_inicio    DATE,
    data_fim       DATE,
    status         VARCHAR(50)  NOT NULL
);

-- ================================================================
-- SUPPORT DOMAIN
-- ================================================================

CREATE TABLE tb_tickets (
    id               BIGSERIAL PRIMARY KEY,
    id_usuario       BIGINT       NOT NULL,
    titulo_ticket    VARCHAR(255) NOT NULL,
    descricao_ticket TEXT,
    categoria        VARCHAR(50),
    status           VARCHAR(50),
    data_abertura    DATE,
    data_atualizacao DATE,
    data_fechamento  DATE
);

CREATE TABLE tb_feedbacks (
    id        BIGSERIAL PRIMARY KEY,
    titulo    VARCHAR(255) NOT NULL,
    descricao TEXT,
    tipo      VARCHAR(50)
);

CREATE TABLE tb_feedback_usuario (
    id           BIGSERIAL PRIMARY KEY,
    id_feedback  BIGINT      NOT NULL REFERENCES tb_feedbacks(id),
    id_usuario   BIGINT      NOT NULL,
    nota         INTEGER,
    data_criacao DATE
);

-- ================================================================
-- PROGRESSION DOMAIN
-- ================================================================

CREATE TABLE tb_metas (
    id          BIGSERIAL PRIMARY KEY,
    id_usuario  BIGINT         NOT NULL,
    nome_meta   VARCHAR(255),
    valor_meta  NUMERIC(19, 2),
    status      VARCHAR(50),
    tipo        VARCHAR(50)
);

CREATE TABLE tb_progresso_meta (
    id               BIGSERIAL PRIMARY KEY,
    id_meta          BIGINT         NOT NULL UNIQUE REFERENCES tb_metas(id),
    progresso        NUMERIC(19, 4),
    peso             NUMERIC(10, 4),
    ultimo_progresso DATE
);

CREATE TABLE tb_dividas (
    id           BIGSERIAL PRIMARY KEY,
    id_usuario   BIGINT         NOT NULL,
    valor_divida NUMERIC(19, 2),
    status       VARCHAR(50)
);

CREATE TABLE tb_progresso_divida (
    id               BIGSERIAL PRIMARY KEY,
    id_divida        BIGINT         NOT NULL UNIQUE REFERENCES tb_dividas(id),
    progresso        NUMERIC(19, 4),
    peso             NUMERIC(10, 4),
    ultimo_progresso DATE
);

CREATE TABLE tb_mapa_progressao (
    id          BIGSERIAL PRIMARY KEY,
    url_modelo  VARCHAR(500),
    nome_mapa   VARCHAR(255)
);

CREATE TABLE tb_mapa_modulos (
    id                 BIGSERIAL PRIMARY KEY,
    id_mapa_progressao BIGINT         NOT NULL REFERENCES tb_mapa_progressao(id),
    requisitos         NUMERIC(19, 4),
    pos_x              NUMERIC(10, 4),
    pos_y              NUMERIC(10, 4),
    tipo               VARCHAR(50),
    estilo             VARCHAR(50)
);

CREATE TABLE tb_mapa_modulos_detalhes (
    id             BIGSERIAL PRIMARY KEY,
    id_mapa_modulo BIGINT         NOT NULL REFERENCES tb_mapa_modulos(id),
    requisitos     NUMERIC(19, 4),
    pos_x          NUMERIC(10, 4),
    pos_y          NUMERIC(10, 4)
);

CREATE TABLE tb_modulo_progressao_usuario (
    id             BIGSERIAL PRIMARY KEY,
    id_mapa_modulo BIGINT         NOT NULL REFERENCES tb_mapa_modulos(id),
    id_usuario     BIGINT         NOT NULL,
    progressao     NUMERIC(10, 4)
);

-- ================================================================
-- FINANCE DOMAIN
-- ================================================================

CREATE TABLE tb_bancos (
    id            BIGSERIAL PRIMARY KEY,
    titulo        VARCHAR(255),
    agencia       NUMERIC(10, 0),
    criado_em     TIMESTAMP,
    atualizado_em TIMESTAMP
);

CREATE TABLE tb_vendedores (
    id           BIGSERIAL PRIMARY KEY,
    nome_empresa VARCHAR(255),
    cpf_cnpj     VARCHAR(20) UNIQUE
);

CREATE TABLE tb_categoria_compra (
    id             BIGSERIAL PRIMARY KEY,
    id_vendedor    BIGINT       NOT NULL REFERENCES tb_vendedores(id),
    nome_categoria VARCHAR(255)
);

CREATE TABLE tb_conta_bancaria (
    id                 BIGSERIAL PRIMARY KEY,
    id_usuario         BIGINT         NOT NULL,
    id_banco           BIGINT         REFERENCES tb_bancos(id),
    numero_conta       VARCHAR(50),
    tipo_conta         VARCHAR(50),
    conta_conjunta     BOOLEAN        DEFAULT FALSE,
    nome_conta         VARCHAR(255),
    moeda              VARCHAR(10),
    saldo_atual        NUMERIC(19, 2) DEFAULT 0,
    saldo_disponivel   NUMERIC(19, 2) DEFAULT 0,
    limite_credito     NUMERIC(19, 2) DEFAULT 0,
    data_abertura      DATE,
    status_conta       VARCHAR(50),
    ultima_atualizacao TIMESTAMP,
    agencia            NUMERIC(10, 0),
    media_receita      NUMERIC(19, 2) DEFAULT 0,
    media_despesa      NUMERIC(19, 2) DEFAULT 0,
    criado_em          TIMESTAMP,
    atualizado_em      TIMESTAMP
);

CREATE TABLE tb_transacoes (
    id                    BIGSERIAL PRIMARY KEY,
    id_conta_bancaria     BIGINT         NOT NULL REFERENCES tb_conta_bancaria(id),
    id_vendedor           BIGINT         REFERENCES tb_vendedores(id),
    valor                 NUMERIC(19, 2) NOT NULL,
    data_transacao        DATE,
    tipo                  VARCHAR(50),
    descricao             VARCHAR(500),
    descricao_normalizada VARCHAR(500),
    parcelado             BOOLEAN        DEFAULT FALSE,
    total_parcelas        NUMERIC(5,  0),
    numero_parcela        NUMERIC(5,  0),
    metodo_pagamento      VARCHAR(50)
);

CREATE TABLE tb_transacoes_recorrentes (
    id                    BIGSERIAL PRIMARY KEY,
    id_conta_bancaria     BIGINT         NOT NULL REFERENCES tb_conta_bancaria(id),
    id_vendedor           BIGINT         REFERENCES tb_vendedores(id),
    valor                 NUMERIC(19, 2) NOT NULL,
    tipo                  VARCHAR(50),
    descricao_normalizada VARCHAR(500),
    parcelado             BOOLEAN        DEFAULT FALSE
);

-- ================================================================
-- ANALYSIS DOMAIN
-- ================================================================

CREATE TABLE tb_analises (
    id               BIGSERIAL PRIMARY KEY,
    id_usuario       BIGINT      NOT NULL,
    tipo             VARCHAR(50),
    origem           VARCHAR(255),
    resultado_resumo TEXT,
    relevancia       VARCHAR(255),
    data_criacao     TIMESTAMP,
    status           VARCHAR(50),
    periodo_inicio   DATE,
    periodo_fim      DATE,
    acionavel        BOOLEAN     DEFAULT FALSE
);

CREATE TABLE tb_analise_entidade
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    id_analise     BIGINT,
    tipo_entidade  VARCHAR(255),
    id_entidade    BIGINT,
    papel_entidade VARCHAR(255),
    peso_entidade  DECIMAL,
    CONSTRAINT pk_tb_analise_entidade PRIMARY KEY (id)
);

ALTER TABLE tb_analise_entidade
    ADD CONSTRAINT FK_TB_ANALISE_ENTIDADE_ON_ID_ANALISE FOREIGN KEY (id_analise) REFERENCES tb_analises (id);

CREATE TABLE tb_analise_resultado (
    id                BIGSERIAL PRIMARY KEY,
    id_analise        BIGINT         NOT NULL UNIQUE REFERENCES tb_analises(id),
    classificacao     VARCHAR(100),
    score             NUMERIC(10, 4),
    probabilidade     NUMERIC(10, 4),
    coeficiente_geral NUMERIC(10, 4),
    nivel_risco       VARCHAR(50),
    modelo_utilizado  VARCHAR(100),
    versao_modelo     VARCHAR(50),
    explicacao        TEXT,
    data_criacao      TIMESTAMP
);

CREATE TABLE tb_analise_resultado_variavel (
    id                BIGSERIAL PRIMARY KEY,
    id_resultado      BIGINT         NOT NULL REFERENCES tb_analise_resultado(id),
    nome_variavel     VARCHAR(255),
    valor_variavel    VARCHAR(255),
    valor_faixa       NUMERIC(10, 4),
    peso              NUMERIC(10, 4),
    coeficiente       NUMERIC(10, 4),
    impacto_resultado VARCHAR(100),
    faixa_referencia  VARCHAR(255),
    data_registro     DATE
);

CREATE TABLE tb_analise_impacto (
    id                      BIGSERIAL PRIMARY KEY,
    id_analise              BIGINT         NOT NULL REFERENCES tb_analises(id),
    tipo_impacto            VARCHAR(50),
    entidade_origem_tipo    VARCHAR(100),
    entidade_origem_id      BIGINT,
    entidade_impactada_tipo VARCHAR(100),
    entidade_impactada_id   BIGINT,
    descricao               TEXT,
    gravidade               VARCHAR(50),
    score_impacto           NUMERIC(10, 4),
    impacto_estimado_valor  NUMERIC(19, 2),
    impacto_temporal_anual  NUMERIC(19, 2),
    impacto_temporal_mensal NUMERIC(19, 2)
);

CREATE TABLE tb_recomendacoes (
    id           BIGSERIAL PRIMARY KEY,
    id_usuario   BIGINT      NOT NULL,
    id_analise   BIGINT      REFERENCES tb_analises(id),
    tipo         VARCHAR(50),
    titulo       VARCHAR(255),
    descricao    TEXT,
    dificuldade  NUMERIC(5, 2),
    prioridade   VARCHAR(50),
    status       VARCHAR(50),
    data_criacao TIMESTAMP
);

CREATE TABLE tb_recomendacao_entidade (
    id              BIGSERIAL PRIMARY KEY,
    id_recomendacao BIGINT      NOT NULL REFERENCES tb_recomendacoes(id),
    tipo_entidade   VARCHAR(100),
    id_entidade     BIGINT,
    papel_entidade  VARCHAR(100)
);
