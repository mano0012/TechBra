-- Migration V2: Add performance indexes and constraints
-- Author: TechBra Team
-- Date: 2025-01-14
-- Description: Adds additional indexes and constraints for better performance

-- Add composite indexes for common query patterns
CREATE INDEX idx_orders_status_created_at ON orders(status, created_at DESC);
CREATE INDEX idx_orders_customer_created_at ON orders(customer_id, created_at DESC);
CREATE INDEX idx_orders_total_amount ON orders(total_amount);
CREATE INDEX idx_orders_updated_at ON orders(updated_at);

-- Add partial indexes for active orders (performance optimization)
CREATE INDEX idx_orders_active_status ON orders(status, created_at DESC) 
    WHERE status IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED');

-- Add indexes for order items with better query performance
CREATE INDEX idx_order_items_created_at ON order_items(created_at);
CREATE INDEX idx_order_items_updated_at ON order_items(updated_at);
CREATE INDEX idx_order_items_total_price ON order_items(total_price);
CREATE INDEX idx_order_items_quantity ON order_items(quantity);

-- Add constraint to ensure order totals are consistent
ALTER TABLE orders ADD CONSTRAINT chk_orders_amounts_positive 
    CHECK (discount_amount >= 0 AND total_amount >= 0 AND final_amount >= 0);

-- Add constraint to ensure discount doesn't exceed total amount
ALTER TABLE orders ADD CONSTRAINT chk_orders_discount_valid 
    CHECK (discount_amount <= total_amount);

-- Add constraint to ensure order items have positive values
ALTER TABLE order_items ADD CONSTRAINT chk_order_items_amounts_positive 
    CHECK (unit_price > 0 AND total_price > 0);

-- Add constraint to ensure total_price calculation is correct
ALTER TABLE order_items ADD CONSTRAINT chk_order_items_total_calculation 
    CHECK (total_price = unit_price * quantity);

-- Create function to validate order status transitions
CREATE OR REPLACE FUNCTION validate_order_status_transition()
RETURNS TRIGGER AS $$
BEGIN
    -- Allow any status for new orders
    IF TG_OP = 'INSERT' THEN
        RETURN NEW;
    END IF;
    
    -- Validate status transitions for updates
    IF TG_OP = 'UPDATE' AND OLD.status != NEW.status THEN
        -- CANCELLED orders cannot change status
        IF OLD.status = 'CANCELLED' THEN
            RAISE EXCEPTION 'Cannot change status of cancelled order';
        END IF;
        
        -- DELIVERED orders cannot change status
        IF OLD.status = 'DELIVERED' THEN
            RAISE EXCEPTION 'Cannot change status of delivered order';
        END IF;
        
        -- RETURNED orders cannot change status
        IF OLD.status = 'RETURNED' THEN
            RAISE EXCEPTION 'Cannot change status of returned order';
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for order status validation
CREATE TRIGGER trigger_validate_order_status
    BEFORE INSERT OR UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION validate_order_status_transition();

-- Create function to update order totals when items change
CREATE OR REPLACE FUNCTION update_order_totals()
RETURNS TRIGGER AS $$
DECLARE
    order_subtotal DECIMAL(12,2);
    tax_amount DECIMAL(12,2) := 0;
    shipping_amount DECIMAL(12,2) := 0;
BEGIN
    -- Calculate new subtotal for the order
    SELECT COALESCE(SUM(total_price), 0)
    INTO order_subtotal
    FROM order_items
    WHERE order_id = COALESCE(NEW.order_id, OLD.order_id);
    
    -- Update the order totals
    UPDATE orders
    SET subtotal = order_subtotal,
        tax_amount = tax_amount,
        shipping_amount = shipping_amount,
        total_amount = order_subtotal + tax_amount + shipping_amount,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = COALESCE(NEW.order_id, OLD.order_id);
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Create triggers to automatically update order totals
CREATE TRIGGER trigger_update_order_totals_insert
    AFTER INSERT ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION update_order_totals();

CREATE TRIGGER trigger_update_order_totals_update
    AFTER UPDATE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION update_order_totals();

CREATE TRIGGER trigger_update_order_totals_delete
    AFTER DELETE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION update_order_totals();

-- Add comments for new constraints and functions
COMMENT ON CONSTRAINT chk_orders_amounts_positive ON orders IS 'Ensures all monetary amounts are non-negative';
COMMENT ON CONSTRAINT chk_orders_discount_valid ON orders IS 'Ensures discount does not exceed subtotal';
COMMENT ON CONSTRAINT chk_order_items_amounts_positive ON order_items IS 'Ensures unit price and total price are positive';
COMMENT ON CONSTRAINT chk_order_items_total_calculation ON order_items IS 'Ensures total price equals unit price times quantity';

COMMENT ON FUNCTION validate_order_status_transition() IS 'Validates order status transitions according to business rules';
COMMENT ON FUNCTION update_order_totals() IS 'Automatically updates order totals when items are modified';

-- Log migration completion
DO $$
BEGIN
    RAISE NOTICE 'Migration V2__Add_performance_indexes completed successfully at %', NOW();
END $$;