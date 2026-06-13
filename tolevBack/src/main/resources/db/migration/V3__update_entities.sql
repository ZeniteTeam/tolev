-- ================================================================
-- FINANCE DOMAIN
-- ================================================================

-- Categorias de gastos do sistema (catálogo padrão, igual para todos os usuários)
CREATE TABLE tb_categorias_gastos_sistema (
    id    BIGSERIAL PRIMARY KEY,
    nome  VARCHAR(255),
    cor   VARCHAR(20),
    tipo  VARCHAR(20),
    ativo BOOLEAN DEFAULT TRUE
);

-- Categorias de gastos criadas pelo próprio usuário
CREATE TABLE tb_categorias_gastos_usuario (
    id         BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    nome       VARCHAR(255),
    cor        VARCHAR(20),
    tipo       VARCHAR(20),
    ativo      BOOLEAN DEFAULT TRUE
);

-- Vínculo das transações com categorias (sistema ou usuário)
ALTER TABLE tb_transacoes
    ADD COLUMN id_categoria_gasto_sistema BIGINT REFERENCES tb_categorias_gastos_sistema(id),
    ADD COLUMN id_categoria_gasto_usuario  BIGINT REFERENCES tb_categorias_gastos_usuario(id);

-- Transações recorrentes ganham descrição, categorização, recorrência e status
ALTER TABLE tb_transacoes_recorrentes
    ADD COLUMN descricao                  VARCHAR(500),
    ADD COLUMN id_categoria_gasto_sistema BIGINT REFERENCES tb_categorias_gastos_sistema(id),
    ADD COLUMN id_categoria_gasto_usuario  BIGINT REFERENCES tb_categorias_gastos_usuario(id),
    ADD COLUMN metodo_pagamento           VARCHAR(50),
    ADD COLUMN dia_recorrencia            INTEGER,
    ADD COLUMN data_inicio                DATE,
    ADD COLUMN data_fim                   DATE,
    ADD COLUMN ativo                      BOOLEAN DEFAULT TRUE;

-- Vendedores ganham categoria (varejo / físico)
ALTER TABLE tb_vendedores
    ADD COLUMN categoria_vendedor VARCHAR(20);

-- Bancos ganham código e logo
ALTER TABLE tb_bancos
    ADD COLUMN codigo_banco VARCHAR(20),
    ADD COLUMN logo_url     VARCHAR(500);

-- Simulações financeiras
CREATE TABLE tb_simulacao (
    id                 BIGSERIAL PRIMARY KEY,
    id_usuario         BIGINT NOT NULL,
    nome               VARCHAR(255),
    descricao          TEXT,
    tipo               VARCHAR(50),
    parametros_entrada TEXT,
    ativo              BOOLEAN DEFAULT TRUE,
    criado_em          TIMESTAMP
);

CREATE TABLE tb_simulacao_resultado (
    id               BIGSERIAL PRIMARY KEY,
    simulacao_id     BIGINT NOT NULL REFERENCES tb_simulacao(id),
    data_referencia  DATE,
    valor_projetado  NUMERIC(19, 2),
    saldo_projetado  NUMERIC(19, 2),
    economia_gerada  NUMERIC(19, 2),
    observacoes      TEXT,
    criado_em        TIMESTAMP
);

-- ================================================================
-- PROGRESSION DOMAIN
-- ================================================================

-- Metas ganham categoria, prazo, recompensa e motivação
ALTER TABLE tb_metas
    ADD COLUMN categoria      VARCHAR(50),
    ADD COLUMN data_limite    DATE,
    ADD COLUMN recompensa     VARCHAR(255),
    ADD COLUMN motivacao_meta VARCHAR(500);

-- Progresso da meta passa a registrar percentual quitado e data de referência
ALTER TABLE tb_progresso_meta
    ADD COLUMN percentual_quitado DOUBLE PRECISION;
ALTER TABLE tb_progresso_meta
    RENAME COLUMN ultimo_progresso TO data_referencia;

-- Dívidas ganham identificação, credor, datas e nível de comprometimento
ALTER TABLE tb_dividas
    ADD COLUMN nome_divida           VARCHAR(255),
    ADD COLUMN credor                VARCHAR(255),
    ADD COLUMN data_inicio           DATE,
    ADD COLUMN data_vencimento_final DATE,
    ADD COLUMN nivel_comprometimento VARCHAR(20);

-- Parcelas das dívidas
CREATE TABLE tb_parcela_dividas (
    id              BIGSERIAL PRIMARY KEY,
    id_divida       BIGINT NOT NULL REFERENCES tb_dividas(id),
    valor_principal NUMERIC(19, 2),
    valor_total     NUMERIC(19, 2),
    valor_juros     NUMERIC(19, 2),
    numero_parcela  INTEGER,
    status          VARCHAR(20),
    data_pagamento  DATE,
    data_vencimento DATE
);

-- Pagamentos efetuados em cada parcela
CREATE TABLE tb_pagamento_parcela (
    id               BIGSERIAL PRIMARY KEY,
    parcela_divida_id BIGINT NOT NULL REFERENCES tb_parcela_dividas(id),
    valor_pago       NUMERIC(19, 2),
    data_pagamento   DATE,
    observacao       VARCHAR(500)
);

-- Mapa de progressão ganha descrição e flag de ativo
ALTER TABLE tb_mapa_progressao
    ADD COLUMN descricao TEXT,
    ADD COLUMN ativo     BOOLEAN DEFAULT TRUE;

-- Módulos do mapa ganham nome e descrição
ALTER TABLE tb_mapa_modulos
    ADD COLUMN nome      VARCHAR(255),
    ADD COLUMN descricao TEXT;

-- Detalhes do módulo ganham título e conteúdo
ALTER TABLE tb_mapa_modulos_detalhes
    ADD COLUMN titulo   VARCHAR(255),
    ADD COLUMN conteudo TEXT;

-- Progresso do usuário no módulo passa a registrar conclusão
ALTER TABLE tb_modulo_progressao_usuario
    ADD COLUMN concluido      BOOLEAN DEFAULT FALSE,
    ADD COLUMN data_conclusao DATE;
