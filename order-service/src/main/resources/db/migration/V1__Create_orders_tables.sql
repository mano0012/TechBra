-- Migration V1: Create orders tables
-- Author: TechBra Team
-- Date: 2025-01-14
-- Description: Creates the main tables for the order service

-- Create function to automatically update updated_at column
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create orders table
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(19,2) DEFAULT 0.00,
    final_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    customer_notes TEXT,
    internal_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP WITH TIME ZONE,
    shipped_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create order_items table
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_sku VARCHAR(100),
    product_description TEXT,
    product_image_url VARCHAR(500),
    unit_price DECIMAL(12,2) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    total_price DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_customer_status ON orders(customer_id, status);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_order_items_order_product ON order_items(order_id, product_id);

-- Create triggers for updated_at columns
CREATE TRIGGER trigger_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_order_items_updated_at
    BEFORE UPDATE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments to tables and columns
COMMENT ON TABLE orders IS 'Tabela principal de pedidos do sistema';
COMMENT ON COLUMN orders.id IS 'Identificador único do pedido';
COMMENT ON COLUMN orders.order_number IS 'Número do pedido (formato: ORD-YYYYMMDD-NNNN)';
COMMENT ON COLUMN orders.customer_id IS 'ID do cliente que fez o pedido';
COMMENT ON COLUMN orders.status IS 'Status atual do pedido';
COMMENT ON COLUMN orders.total_amount IS 'Valor total do pedido';
COMMENT ON COLUMN orders.discount_amount IS 'Valor do desconto aplicado';
COMMENT ON COLUMN orders.final_amount IS 'Valor final do pedido após desconto';
COMMENT ON COLUMN orders.customer_notes IS 'Observações do cliente';
COMMENT ON COLUMN orders.internal_notes IS 'Observações internas';
COMMENT ON COLUMN orders.confirmed_at IS 'Data e hora de confirmação do pedido';
COMMENT ON COLUMN orders.shipped_at IS 'Data e hora de envio do pedido';
COMMENT ON COLUMN orders.delivered_at IS 'Data e hora de entrega do pedido';
COMMENT ON COLUMN orders.cancelled_at IS 'Data e hora de cancelamento do pedido';
COMMENT ON COLUMN orders.version IS 'Versão para controle de concorrência otimista';

COMMENT ON TABLE order_items IS 'Itens dos pedidos';
COMMENT ON COLUMN order_items.id IS 'Identificador único do item';
COMMENT ON COLUMN order_items.order_id IS 'ID do pedido ao qual o item pertence';
COMMENT ON COLUMN order_items.product_id IS 'ID do produto';
COMMENT ON COLUMN order_items.product_name IS 'Nome do produto no momento do pedido';
COMMENT ON COLUMN order_items.product_sku IS 'SKU do produto';
COMMENT ON COLUMN order_items.product_description IS 'Descrição do produto';
COMMENT ON COLUMN order_items.product_image_url IS 'URL da imagem do produto';
COMMENT ON COLUMN order_items.unit_price IS 'Preço unitário do produto';
COMMENT ON COLUMN order_items.quantity IS 'Quantidade do produto';
COMMENT ON COLUMN order_items.total_price IS 'Preço total do item (unit_price * quantity)';
COMMENT ON COLUMN order_items.version IS 'Versão para controle de concorrência otimista';

-- Insert some sample data for testing (optional)
-- This can be removed in production
INSERT INTO orders (
    order_number, customer_id, status, total_amount, discount_amount, final_amount, customer_notes
) VALUES (
    'ORD-20250114-0001',
    gen_random_uuid(),
    'PENDING',
    100.00,
    0.00,
    100.00,
    'Pedido de teste'
);

-- Log migration completion
DO $$
BEGIN
    RAISE NOTICE 'Migration V1__Create_orders_tables completed successfully at %', NOW();
END $$;