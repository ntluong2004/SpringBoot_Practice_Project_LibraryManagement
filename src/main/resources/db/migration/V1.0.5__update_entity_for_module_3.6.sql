-- 1. Thêm cột cho book_copy
ALTER TABLE book_copy ADD COLUMN is_locked BOOLEAN DEFAULT FALSE;

-- 2. Cập nhật bảng loan (Xóa return_status vì đã có, chỉ thêm staff_note)
