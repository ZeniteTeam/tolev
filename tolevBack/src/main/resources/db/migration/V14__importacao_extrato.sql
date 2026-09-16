-- ================================================================
-- IMPORTAÇÃO DE EXTRATO BANCÁRIO
--
-- O usuário sobe o PDF do extrato, o Gemini extrai os lançamentos e
-- eles viram transações reais em tb_transacoes. Duas coisas precisam
-- ser lembradas entre um upload e o próximo:
--
-- 1. Até que data já foi importado, por banco. Sem isso, subir o
--    extrato de setembro depois do de agosto reimportaria agosto
--    inteiro e dobraria os gastos do mês na análise. O marco é por
--    banco (e não por usuário) porque o extrato do Itaú não tem nada
--    a dizer sobre o que já veio do Nubank — um marco global recusaria
--    o segundo banco sem motivo.
--
--    Não dá para derivar o marco de MAX(data_transacao): isso incluiria
--    lançamentos digitados à mão, que bloqueariam a importação de um
--    período que nunca foi importado.
--
-- 2. Quais transações vieram de qual upload. Como a importação grava
--    direto, sem tela de revisão, um PDF lido errado precisa poder ser
--    desfeito de uma vez — e apagar transação por transação não é opção
--    (nem existe DELETE /transactions hoje).
--
-- A conta bancária fica de fora de propósito: o extrato descreve o que
-- já aconteceu, e o saldo de tb_conta_bancaria pode estar desatualizado.
-- Reaplicar cada linha no saldo contaria o mesmo dinheiro duas vezes.
-- ================================================================

CREATE TABLE tb_importacoes_extrato (
    id                   BIGSERIAL PRIMARY KEY,
    id_usuario           BIGINT  NOT NULL,
    id_banco             BIGINT  NOT NULL REFERENCES tb_bancos (id),
    -- Período coberto pelo que foi de fato gravado, não pelo PDF: se
    -- metade do extrato caiu no filtro do marco, o início é o primeiro
    -- lançamento que sobrou.
    data_inicio          DATE    NOT NULL,
    data_fim             DATE    NOT NULL,
    quantidade_importada INTEGER NOT NULL,
    -- Quantos o Gemini leu mas já estavam cobertos pelo marco anterior.
    -- Guardado para a resposta do endpoint poder explicar o que sumiu.
    quantidade_pulada    INTEGER NOT NULL,
    criado_em            TIMESTAMP NOT NULL
);

-- A consulta do marco é sempre "o último data_fim deste usuário neste
-- banco", feita uma vez por upload.
CREATE INDEX idx_importacoes_extrato_usuario_banco
    ON tb_importacoes_extrato (id_usuario, id_banco, data_fim DESC);

-- Procedência da transação. NULL = lançada à mão pelo usuário, que é o
-- caso de tudo que já existe.
ALTER TABLE tb_transacoes
    ADD COLUMN id_importacao_extrato BIGINT REFERENCES tb_importacoes_extrato (id);

-- Desfazer uma importação apaga por esta coluna.
CREATE INDEX idx_transacoes_importacao_extrato
    ON tb_transacoes (id_importacao_extrato)
    WHERE id_importacao_extrato IS NOT NULL;
