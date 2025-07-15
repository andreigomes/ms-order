-- Adiciona coluna version para controle de concorrência otimista
ALTER TABLE orders ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

