-- Script para criação do banco de dados e tabelas
-- PostgreSQL

-- Criar banco de dados (executar separadamente)
-- CREATE DATABASE seguradora_orders;
-- CREATE USER seguradora_user WITH PASSWORD 'seguradora_pass';
-- GRANT ALL PRIVILEGES ON DATABASE seguradora_orders TO seguradora_user;

-- Usar o banco seguradora_orders
-- \c seguradora_orders;

-- Criar tabela de pedidos
CREATE TABLE orders (
    id VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    category VARCHAR(50) NOT NULL, -- AUTO, VIDA, RESIDENCIAL, EMPRESARIAL, etc.
    sales_channel VARCHAR(50) NOT NULL, -- MOBILE, WHATSAPP, WEB_SITE, etc.
    payment_method VARCHAR(50) NOT NULL, -- CREDIT_CARD, DEBIT_ACCOUNT, BOLETO, PIX
    total_monthly_premium_amount DECIMAL(15,2) NOT NULL,
    insured_amount DECIMAL(15,2) NOT NULL,
    coverages JSONB, -- Para PostgreSQL
    assistances JSONB, -- Para PostgreSQL
    description TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP NULL,
    history JSONB, -- Para PostgreSQL
    payment_approved VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    subscription_approved VARCHAR(50) NOT NULL DEFAULT 'PENDING'
);

-- Comentários para documentação
COMMENT ON COLUMN orders.payment_approved IS 'Status de aprovação do pagamento: PENDING, APPROVED, REJECTED';
COMMENT ON COLUMN orders.subscription_approved IS 'Status de aprovação da subscrição: PENDING, APPROVED, REJECTED';

-- Criar índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_product_id ON orders(product_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_category ON orders(category);
CREATE INDEX IF NOT EXISTS idx_orders_sales_channel ON orders(sales_channel);
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