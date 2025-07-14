-- Adicionar campos para coordenação de eventos
-- Estes campos podem ser usados para rastreamento de eventos e correlação

ALTER TABLE orders ADD COLUMN IF NOT EXISTS risk_level VARCHAR(50);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS fraud_score DECIMAL(5,2);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS validation_reason TEXT;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS last_event_timestamp TIMESTAMP;

-- Criar índice para o risk_level
CREATE INDEX IF NOT EXISTS idx_orders_risk_level ON orders(risk_level);
