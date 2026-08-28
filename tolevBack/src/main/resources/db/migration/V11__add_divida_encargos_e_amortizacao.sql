-- ================================================================
-- DIVIDAS - Encargos e sistema de amortização
-- Campos coletados no novo fluxo de "Adicionar dívida", que passam a
-- alimentar o cálculo real do parcelamento (PRICE/SAC, simples/composto).
--
-- data_inicio (criada em V3 e nunca preenchida) vira data_liberacao, que
-- é exatamente o que ela representa: quando o valor foi liberado.
-- ================================================================

ALTER TABLE tb_dividas
    RENAME COLUMN data_inicio TO data_liberacao;

ALTER TABLE tb_dividas
    ADD COLUMN data_primeiro_vencimento DATE,
    ADD COLUMN multa_atraso             NUMERIC(19, 2),
    ADD COLUMN juros_mora               NUMERIC(19, 2),
    ADD COLUMN sistema_amortizacao      VARCHAR(20),
    ADD COLUMN regime_juros             VARCHAR(20);

-- Dívidas já cadastradas foram criadas com parcelas iguais (saldo ÷ nº de
-- parcelas), o que equivale a PRICE sem juros. Marcar o padrão evita nulos
-- no cálculo e mantém o comportamento delas inalterado.
UPDATE tb_dividas
SET sistema_amortizacao = 'PRICE',
    regime_juros        = 'COMPOSTO'
WHERE sistema_amortizacao IS NULL;
