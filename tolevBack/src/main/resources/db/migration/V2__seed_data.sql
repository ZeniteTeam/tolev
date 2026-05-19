-- ================================================================
-- ASSINATURAS
-- Planos de assinatura da plataforma. Devem existir antes de
-- qualquer usuário poder se inscrever.
-- ================================================================

INSERT INTO tb_assinaturas (modelo_assinatura) VALUES
    ('GRATUITO'),
    ('PLUS'),
    ('PREMIUM');


-- ================================================================
-- BANCOS
-- Principais bancos brasileiros. Necessários para que o usuário
-- consiga cadastrar suas contas bancárias.
-- ================================================================

INSERT INTO tb_bancos (titulo, agencia, criado_em, atualizado_em) VALUES
    ('Banco do Brasil',        1,   NOW(), NOW()),
    ('Caixa Econômica Federal', 104, NOW(), NOW()),
    ('Bradesco',               237, NOW(), NOW()),
    ('Itaú Unibanco',          341, NOW(), NOW()),
    ('Santander Brasil',        33, NOW(), NOW()),
    ('Nubank',                 260, NOW(), NOW()),
    ('Inter',                  77,  NOW(), NOW());


-- ================================================================
-- FEEDBACKS
-- Templates de feedback — um por TipoFeedback.
-- FeedbackUsuario referencia esses registros; sem eles o vínculo
-- não pode ser criado.
-- ================================================================

INSERT INTO tb_feedbacks (titulo, descricao, tipo) VALUES
    ('Reportar um bug',          'Encontrou algo que não está funcionando? Descreva o problema.',          'BUG'),
    ('Enviar sugestão',          'Tem uma ideia para melhorar a plataforma? Conta pra gente.',             'SUGESTAO'),
    ('Avaliar sua experiência',  'Como está sendo sua experiência com o Tolev até agora?',                 'EXPERIENCIA'),
    ('Solicitar suporte',        'Precisa de ajuda? Descreva sua dúvida e entraremos em contato.',         'SUPORTE');


-- ================================================================
-- MAPA DE PROGRESSÃO
-- Conteúdo definido pelo produto — não gerado por usuário.
-- ================================================================

INSERT INTO tb_mapa_progressao (url_modelo, nome_mapa) VALUES
    ('assets/maps/jornada_financeira.json', 'Jornada Financeira');


-- ================================================================
-- MÓDULOS DO MAPA
-- Nós do mapa de progressão com posição e tipo.
-- ================================================================

INSERT INTO tb_mapa_modulos (id_mapa_progressao, requisitos, pos_x, pos_y, tipo, estilo) VALUES
    (1, 0,    1.0, 1.0, 'PROGRESSAO', 'CIDADE'),
    (1, 10,   1.0, 2.0, 'EDUCACAO',   'CIDADE'),
    (1, 25,   2.0, 2.5, 'DESAFIO',    'FLORESTA'),
    (1, 40,   2.0, 3.5, 'PROGRESSAO', 'FLORESTA'),
    (1, 55,   3.0, 4.0, 'EDUCACAO',   'RIO'),
    (1, 70,   3.0, 5.0, 'DESAFIO',    'RIO'),
    (1, 85,   4.0, 5.5, 'BONUS',      'MONTANHA'),
    (1, 100,  4.0, 6.5, 'PROGRESSAO', 'MONTANHA');
