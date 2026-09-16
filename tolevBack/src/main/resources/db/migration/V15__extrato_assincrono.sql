-- ================================================================
-- IMPORTAÇÃO DE EXTRATO EM SEGUNDO PLANO
--
-- A leitura do PDF pelo Gemini leva dezenas de segundos: um extrato
-- mensal tem centenas de linhas e o modelo lê o documento inteiro
-- antes de responder. Segurar a requisição HTTP por todo esse tempo
-- deixaria o app travado numa tela de espera, e qualquer queda de
-- rede no meio perderia o trabalho já pago à API.
--
-- Por isso a importação deixa de ser "uma requisição que devolve o
-- resultado" e passa a ser um registro com estado próprio: o POST
-- grava a linha em PROCESSANDO e devolve na hora, o processamento
-- roda numa thread separada e escreve o desfecho aqui. O app pergunta
-- o estado quando quiser — inclusive depois de ser fechado e reaberto,
-- que é o ponto de guardar isso em tabela e não em memória.
--
-- Consequência direta: os campos do resultado (período, contagens,
-- totais) só existem depois de CONCLUIDA e precisam aceitar NULL
-- enquanto o extrato está sendo lido.
-- ================================================================

ALTER TABLE tb_importacoes_extrato
    ALTER COLUMN data_inicio          DROP NOT NULL,
    ALTER COLUMN data_fim             DROP NOT NULL,
    ALTER COLUMN quantidade_importada DROP NOT NULL,
    ALTER COLUMN quantidade_pulada    DROP NOT NULL;

ALTER TABLE tb_importacoes_extrato
    -- PROCESSANDO -> CONCLUIDA | FALHOU. O default cobre as linhas que
    -- já existirem: elas vieram do fluxo síncrono, logo já terminaram.
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'CONCLUIDA',

    -- Mostrado na tela de acompanhamento. Sem isso, quem sobe dois
    -- extratos seguidos não distingue um do outro na lista.
    ADD COLUMN nome_arquivo VARCHAR(255),

    -- Motivo da falha em português, pronto para a tela. A stack fica no
    -- log; o usuário precisa saber se reenvia o mesmo PDF ou outro.
    ADD COLUMN erro VARCHAR(500),

    ADD COLUMN concluido_em TIMESTAMP,

    -- Quando o usuário clicou em "Atualizar" e viu o resultado. Enquanto
    -- for NULL o app mostra o aviso de extrato pronto — inclusive se ele
    -- tiver fechado o app antes de ver.
    ADD COLUMN confirmado_em TIMESTAMP,

    -- O que o Gemini leu antes do filtro do marco, e quantos ficaram sem
    -- categoria por incoerência com o tipo. Guardados porque a resposta
    -- do POST não existe mais para carregá-los: quem pergunta o resultado
    -- é um GET, minutos depois.
    ADD COLUMN quantidade_lida          INTEGER,
    ADD COLUMN quantidade_sem_categoria INTEGER,

    ADD COLUMN total_entradas NUMERIC(19, 2),
    ADD COLUMN total_saidas   NUMERIC(19, 2),

    -- Até onde este banco já estava importado quando o upload chegou.
    -- Explica ao usuário por que parte do extrato foi pulada.
    ADD COLUMN marco_anterior DATE;

ALTER TABLE tb_importacoes_extrato ALTER COLUMN status DROP DEFAULT;

-- A tela de acompanhamento pede "as importações deste usuário, mais
-- recentes primeiro", e o app pergunta isso em intervalo curto enquanto
-- houver uma em PROCESSANDO.
CREATE INDEX idx_importacoes_extrato_usuario_criado
    ON tb_importacoes_extrato (id_usuario, criado_em DESC);


-- ================================================================
-- DE QUAL BANCO VEIO CADA TRANSAÇÃO
--
-- O extrato é sempre de um banco só, escolhido pelo usuário no upload.
-- Guardar isso na transação é o que transforma o filtro por banco da
-- tela de Finanças em filtro de verdade, e é a única informação de
-- banco que temos com certeza — o app não conecta contas.
--
-- Fica separado de id_conta_bancaria de propósito: aquilo é uma conta
-- do usuário, com saldo, que a importação não cria nem movimenta. Isto
-- aqui é só a procedência do lançamento.
--
-- NULL = lançamento digitado à mão, que é o caso de tudo que já existe.
-- ================================================================

ALTER TABLE tb_transacoes
    ADD COLUMN id_banco BIGINT REFERENCES tb_bancos (id);

CREATE INDEX idx_transacoes_usuario_banco
    ON tb_transacoes (id_usuario, id_banco)
    WHERE id_banco IS NOT NULL;


-- ================================================================
-- CATÁLOGO DE BANCOS
--
-- O seletor de banco do upload lê tb_bancos, então o catálogo precisa
-- cobrir os bancos que o app oferece. Faltavam dois digitais bastante
-- comuns, e nenhum dos sete existentes tinha codigo_banco preenchido
-- (a coluna nasceu vazia na V3) — sem ele não há como casar o registro
-- com a identidade visual do app.
-- ================================================================

UPDATE tb_bancos SET codigo_banco = '001' WHERE titulo = 'Banco do Brasil';
UPDATE tb_bancos SET codigo_banco = '104' WHERE titulo = 'Caixa Econômica Federal';
UPDATE tb_bancos SET codigo_banco = '237' WHERE titulo = 'Bradesco';
UPDATE tb_bancos SET codigo_banco = '341' WHERE titulo = 'Itaú Unibanco';
UPDATE tb_bancos SET codigo_banco = '033' WHERE titulo = 'Santander Brasil';
UPDATE tb_bancos SET codigo_banco = '260' WHERE titulo = 'Nubank';
UPDATE tb_bancos SET codigo_banco = '077' WHERE titulo = 'Inter';

INSERT INTO tb_bancos (titulo, codigo_banco, agencia, criado_em, atualizado_em)
SELECT v.titulo, v.codigo, v.agencia, NOW(), NOW()
FROM (VALUES
    ('C6 Bank', '336', 336),
    ('PicPay',  '380', 380)
) AS v (titulo, codigo, agencia)
WHERE NOT EXISTS (SELECT 1 FROM tb_bancos b WHERE b.titulo = v.titulo);
