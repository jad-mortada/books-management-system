-- Books Management System - One-time cleanup for legacy approved temp drafts
-- NOTE: Backup your database before running any DELETEs.
-- Use these statements only if you have lingering approved temp drafts from before the approval fix.

-- 1) Inspect potential lingering approved temp drafts
SELECT id, customer_id, status, created_at, updated_at
FROM temp_customer_orders
WHERE status = 'APPROVED';

-- 2) Inspect temp items that belong to any approved temp drafts (should be empty after fix)
SELECT tcoi.*
FROM temp_customer_order_items tcoi
JOIN temp_customer_orders tco ON tcoi.temp_customer_order_id = tco.id
WHERE tco.status = 'APPROVED';

-- 3) If you confirm these are safe to remove, you can delete them.
--    Due to foreign keys and orphanRemoval, deleting parents should remove children.
--    If your schema doesn't cascade, you may need to delete items first.

-- BEGIN TRANSACTION; -- if your client supports transaction blocks

-- Optional: delete items explicitly if cascade is not configured in DB
-- DELETE tcoi
-- FROM temp_customer_order_items tcoi
-- JOIN temp_customer_orders tco ON tcoi.temp_customer_order_id = tco.id
-- WHERE tco.status = 'APPROVED';

-- Delete approved temp drafts
-- DELETE FROM temp_customer_orders WHERE status = 'APPROVED';

-- COMMIT; -- end transaction
