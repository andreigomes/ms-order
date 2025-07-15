-- Migration para adicionar campos de coordenação de eventos
-- V2__Add_event_coordination_fields.sql

ALTER TABLE orders
ADD COLUMN payment_approved BOOLEAN,
ADD COLUMN subscription_approved BOOLEAN;

-- Comentários para documentação
COMMENT ON COLUMN orders.payment_approved IS 'Status de aprovação do pagamento: null=não processado, true=aprovado, false=rejeitado';
COMMENT ON COLUMN orders.subscription_approved IS 'Status de aprovação da subscrição: null=não processado, true=aprovado, false=rejeitado';
