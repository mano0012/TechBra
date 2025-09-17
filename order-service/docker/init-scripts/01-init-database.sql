-- Script de inicialização do banco de dados para Order Service
-- Este script é executado automaticamente quando o container PostgreSQL é criado

-- Criar extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";

-- Configurar timezone
SET timezone = 'America/Sao_Paulo';

-- Criar schema para auditoria (se necessário)
CREATE SCHEMA IF NOT EXISTS audit;

-- Função para auditoria de timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Comentários sobre o banco
COMMENT ON DATABASE order_service_db IS 'Banco de dados do serviço de pedidos - TechBra E-commerce';

-- Log de inicialização
DO $$
BEGIN
    RAISE NOTICE 'Order Service Database initialized successfully at %', NOW();
END $$;