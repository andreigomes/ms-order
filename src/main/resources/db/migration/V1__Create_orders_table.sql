-- Script para criação do banco de dados e tabelas
-- PostgreSQL

-- Criar banco de dados (executar separadamente)
-- CREATE DATABASE seguradora_orders;
-- CREATE USER seguradora_user WITH PASSWORD 'seguradora_pass';
-- GRANT ALL PRIVILEGES ON DATABASE seguradora_orders TO seguradora_user;

-- Usar o banco seguradora_orders
-- \c seguradora_orders;

-- Criar tabela de pedidos
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    insurance_type VARCHAR(50) NOT NULL CHECK (insurance_type IN ('AUTO', 'HOME', 'LIFE', 'HEALTH', 'TRAVEL', 'BUSINESS')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'PROCESSING', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED')),
    amount DECIMAL(19,2) NOT NULL CHECK (amount > 0),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Criar índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_insurance_type ON orders(insurance_type);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);

-- Criar função para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Criar trigger para atualizar updated_at
DROP TRIGGER IF EXISTS update_orders_updated_at ON orders;
CREATE TRIGGER update_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comentários nas tabelas e colunas
COMMENT ON TABLE orders IS 'Tabela de pedidos de seguro';
COMMENT ON COLUMN orders.id IS 'Identificador único do pedido';
COMMENT ON COLUMN orders.customer_id IS 'Identificador do cliente';
COMMENT ON COLUMN orders.insurance_type IS 'Tipo de seguro';
COMMENT ON COLUMN orders.status IS 'Status do pedido';
COMMENT ON COLUMN orders.amount IS 'Valor do pedido';
COMMENT ON COLUMN orders.description IS 'Descrição do pedido';
COMMENT ON COLUMN orders.created_at IS 'Data de criação do pedido';
COMMENT ON COLUMN orders.updated_at IS 'Data da última atualização do pedido';
