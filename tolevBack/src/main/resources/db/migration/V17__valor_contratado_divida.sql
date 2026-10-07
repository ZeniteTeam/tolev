-- ================================================================
-- VALOR CONTRATADO DA DÍVIDA
--
-- `valor_divida` faz dois trabalhos: nasce como o principal contratado
-- e depois é decrementado a cada amortização, até ser zerado quando a
-- dívida quita. Quem lê como "valor contratado" erra cada vez mais à
-- medida que o usuário paga — e erra por completo no fim.
--
-- O sintoma: o custo dos juros era calculado como
-- (soma das parcelas − valor_divida). Com a dívida quitada,
-- valor_divida = 0, e o "custo dos juros" virava o valor total da
-- dívida inteira.
--
-- Esta coluna guarda o principal contratado e nunca muda.
-- ================================================================

ALTER TABLE tb_dividas
    ADD COLUMN valor_contratado NUMERIC(19, 2);

-- Backfill pela tabela de parcelas, que é imutável: a soma do principal
-- de todas as parcelas é o principal que a tabela amortiza.
--
-- Fica a menos do valor original exatamente pelo ajuste de primeiro
-- período (o principal é capitalizado antes de a tabela ser montada,
-- então esse pedaço entra como principal e não como juros). Para as
-- dívidas já gravadas é a melhor aproximação disponível, e é muito
-- melhor do que o valor corrente: aquele já foi consumido pelos
-- pagamentos. Dívida nova grava o valor exato na criação.
UPDATE tb_dividas d
SET valor_contratado = COALESCE(
    (SELECT SUM(p.valor_principal)
       FROM tb_parcela_dividas p
      WHERE p.id_divida = d.id),
    d.valor_divida);
