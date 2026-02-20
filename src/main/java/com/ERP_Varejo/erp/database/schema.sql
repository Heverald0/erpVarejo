-- Criação da tabela de Usuários (necessário para o login inicial)
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    login VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    cargo VARCHAR(20) DEFAULT 'OPERADOR'
);

-- Tabela de Fornecedores
CREATE TABLE IF NOT EXISTS fornecedores (
    id SERIAL PRIMARY KEY,
    nome_fantasia VARCHAR(100) NOT NULL,
    cnpj VARCHAR(18) UNIQUE,
    contato VARCHAR(50)
);

-- Tabela de Produtos (Interligada ao estoque)
CREATE TABLE IF NOT EXISTS produtos (
    id SERIAL PRIMARY KEY,
    codigo_barras VARCHAR(50) UNIQUE,
    nome VARCHAR(100) NOT NULL,
    quantidade_estoque INT DEFAULT 0,
    preco_custo DECIMAL(10, 2),
    preco_venda DECIMAL(10, 2) NOT NULL,
    id_fornecedor INT REFERENCES fornecedores(id)
);

-- Tabela de Vendas
CREATE TABLE IF NOT EXISTS vendas (
    id SERIAL PRIMARY KEY,
    data_venda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT REFERENCES usuarios(id),
    metodo_pagamento VARCHAR(20),
    total_venda DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'CONCLUIDA'
);

-- Itens de cada venda
CREATE TABLE IF NOT EXISTS item_venda (
    id SERIAL PRIMARY KEY,
    id_venda INT REFERENCES vendas(id),
    id_produto INT REFERENCES produtos(id),
    quantidade INT NOT NULL,
    preco_unitario_historico DECIMAL(10, 2) NOT NULL
);

-- Credenciais de administrador
INSERT INTO usuarios (nome, login, senha, cargo) 
VALUES ('Heveraldo Admin', 'admin', '1234', 'ADMIN')
ON CONFLICT (login) DO NOTHING;