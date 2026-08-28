-- ================================================================
-- ANALYSIS DOMAIN - Identidade estável de um achado
--
-- Uma análise é recalculada a cada mudança financeira do usuário, então o
-- mesmo achado ("delivery acima do orçamento") reaparece em análises de dias
-- diferentes. Sem uma chave estável não dá para contar recorrência nem para
-- evitar recriar a mesma recomendação todo dia.
--
-- `regra` guarda o nome da constante de RegraAnalise. As colunas existentes
-- não servem: tipo_impacto só tem 5 valores grossos (FINANCEIRO/META/DIVIDA/
-- RISCO/COMPORTAMENTO), então duas regras sobre a mesma dívida colidiriam.
-- ================================================================

ALTER TABLE tb_analise_impacto
    ADD COLUMN regra VARCHAR(100);

ALTER TABLE tb_recomendacoes
    ADD COLUMN regra VARCHAR(100);

-- Contagem de recorrência: quantos dias distintos o achado apareceu.
CREATE INDEX idx_analise_impacto_regra
    ON tb_analise_impacto (regra, entidade_origem_id);

-- Deduplicação: já existe recomendação aberta para essa regra/usuário?
CREATE INDEX idx_recomendacoes_usuario_regra
    ON tb_recomendacoes (id_usuario, regra, status);

-- A análise é reescrita no mesmo dia (uma linha por tipo por dia), então a
-- busca "análise de hoje desse tipo" acontece a cada evento.
CREATE INDEX idx_analises_usuario_tipo_data
    ON tb_analises (id_usuario, tipo, data_criacao);
