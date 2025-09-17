-- Migration V3: Fix order totals function
-- Author: TechBra Team
-- Date: 2025-09-17
-- Description: Fixes the update_order_totals function to use correct column names

-- Drop existing function and triggers
DROP TRIGGER IF EXISTS trigger_update_order_totals_insert ON order_items;
DROP TRIGGER IF EXISTS trigger_update_order_totals_update ON order_items;
DROP TRIGGER IF EXISTS trigger_update_order_totals_delete ON order_items;
DROP FUNCTION IF EXISTS update_order_totals();

-- Create corrected function to update order totals when items change
CREATE OR REPLACE FUNCTION update_order_totals()
RETURNS TRIGGER AS $$
DECLARE
    order_subtotal DECIMAL(12,2);
BEGIN
    -- Calculate new subtotal for the order
    SELECT COALESCE(SUM(total_price), 0)
    INTO order_subtotal
    FROM order_items
    WHERE order_id = COALESCE(NEW.order_id, OLD.order_id);
    
    -- Update the order's total_amount using existing columns only
    UPDATE orders
    SET total_amount = order_subtotal,
        final_amount = order_subtotal - COALESCE(discount_amount, 0),
        updated_at = CURRENT_TIMESTAMP
    WHERE id = COALESCE(NEW.order_id, OLD.order_id);
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Recreate triggers to automatically update order totals
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

-- Add comment
COMMENT ON FUNCTION update_order_totals() IS 'Automatically updates order totals when items are modified - Fixed version';

-- Log migration completion
DO $$
BEGIN
    RAISE NOTICE 'Migration V3__Fix_order_totals_function completed successfully at %', NOW();
END $$;