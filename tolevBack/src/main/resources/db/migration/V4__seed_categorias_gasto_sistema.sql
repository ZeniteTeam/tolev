-- ================================================================
-- CATEGORIAS DE GASTO DO SISTEMA
-- Catálogo padrão de categorias oferecido a todos os usuários.
-- ================================================================

INSERT INTO tb_categorias_gastos_sistema (nome, cor, tipo, ativo) VALUES
    ('Alimentação',      '#F59E0B', 'DESPESA', TRUE),
    ('Transporte',           '#3B82F6', 'DESPESA', TRUE),
    ('Moradia',             '#8B5CF6', 'DESPESA', TRUE),
    ('Saúde',        '#EF4444', 'DESPESA', TRUE),
    ('Educação',            '#10B981', 'DESPESA', TRUE),
    ('Lazer',          '#EC4899', 'DESPESA', TRUE),
    ('Compras',     '#F97316', 'DESPESA', TRUE),
    ('Assinaturas',        '#6366F1', 'DESPESA', TRUE),
    ('Contas e Serviços',     '#14B8A6', 'DESPESA', TRUE),
    ('Outros',     '#6B7280', 'DESPESA', TRUE),
    ('Salário',                '#22C55E', 'RECEITA', TRUE),
    ('Outras Receitas',   '#6B7280', 'RECEITA', TRUE);
