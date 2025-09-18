-- Script de inicialização do banco de dados
-- TechBra E-commerce - Billing Service Database Initialization

-- Conectar ao banco padrão para criar o banco de dados
\c postgres;

-- Criar banco de dados se não existir
SELECT 'CREATE DATABASE billing_service_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'billing_service_db')\gexec

-- Conectar ao banco de dados criado
\c billing_service_db;

-- Criar extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";

-- Configurar timezone
SET timezone = 'America/Sao_Paulo';

-- Criar schema se não existir
CREATE SCHEMA IF NOT EXISTS billing;

-- Definir search_path
SET search_path TO billing, public;

-- Comentário no banco de dados
COMMENT ON DATABASE billing_service_db IS 'Banco de dados do serviço de cobrança e pagamentos - TechBra E-commerce';

-- Criar função para logs de auditoria
CREATE OR REPLACE FUNCTION billing.audit_trigger_function()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO billing.audit_log (table_name, operation, new_data, created_at)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(NEW), CURRENT_TIMESTAMP);
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO billing.audit_log (table_name, operation, old_data, new_data, created_at)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(OLD), row_to_json(NEW), CURRENT_TIMESTAMP);
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO billing.audit_log (table_name, operation, old_data, created_at)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(OLD), CURRENT_TIMESTAMP);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Criar tabela de auditoria
CREATE TABLE IF NOT EXISTS billing.audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(255) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    old_data JSONB,
    new_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para a tabela de auditoria
CREATE INDEX IF NOT EXISTS idx_audit_log_table_name ON billing.audit_log(table_name);
CREATE INDEX IF NOT EXISTS idx_audit_log_operation ON billing.audit_log(operation);
CREATE INDEX IF NOT EXISTS idx_audit_log_created_at ON billing.audit_log(created_at);

-- Criar usuário específico para a aplicação (se não existir)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'billing_app_user') THEN
        CREATE ROLE billing_app_user WITH LOGIN PASSWORD 'billing_app_pass123';
    END IF;
END
$$;

-- Conceder permissões ao usuário da aplicação
GRANT CONNECT ON DATABASE billing_service_db TO billing_app_user;
GRANT USAGE ON SCHEMA billing TO billing_app_user;
GRANT USAGE ON SCHEMA public TO billing_app_user;
GRANT CREATE ON SCHEMA billing TO billing_app_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA billing TO billing_app_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA billing TO billing_app_user;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA billing TO billing_app_user;

-- Configurações de performance
ALTER SYSTEM SET shared_preload_libraries = 'pg_stat_statements';
ALTER SYSTEM SET max_connections = 200;
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';
ALTER SYSTEM SET checkpoint_completion_target = 0.9;
ALTER SYSTEM SET wal_buffers = '16MB';
ALTER SYSTEM SET default_statistics_target = 100;
ALTER SYSTEM SET random_page_cost = 1.1;
ALTER SYSTEM SET effective_io_concurrency = 200;

-- Configurações de logging
ALTER SYSTEM SET log_destination = 'stderr';
ALTER SYSTEM SET logging_collector = on;
ALTER SYSTEM SET log_directory = 'pg_log';
ALTER SYSTEM SET log_filename = 'postgresql-%Y-%m-%d_%H%M%S.log';
ALTER SYSTEM SET log_statement = 'mod';
ALTER SYSTEM SET log_min_duration_statement = 1000;

-- Mensagem de sucesso
\echo 'Banco de dados billing_service_db inicializado com sucesso!';
\echo 'Usuário billing_app_user criado com permissões adequadas.';
\echo 'Extensões e configurações aplicadas.';
\echo 'Sistema pronto para uso do Billing Service.';