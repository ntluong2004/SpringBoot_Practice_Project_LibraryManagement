-- -- 1. Thêm cột mới is_locked với giá trị mặc định là false (0)
-- ALTER TABLE book_copy
--     ADD COLUMN is_locked TINYINT(1) DEFAULT 0;

-- 2. Xử lý chuyển đổi từ shelf_location (String) sang shelf_id (Liên kết bảng shelf)
-- Bước A: Thêm cột shelf_id
-- ALTER TABLE book_copy
--     ADD COLUMN shelf_id BIGINT;
--
-- -- Bước B: Tạo ràng buộc khóa ngoại với bảng shelf
-- ALTER TABLE book_copy
--     ADD CONSTRAINT fk_copy_shelf
--         FOREIGN KEY (shelf_id) REFERENCES shelf(shelf_id);
--
-- -- Bước C: Xóa cột shelf_location cũ (Vì Entity đã chuyển sang dùng Object Shelf)
-- ALTER TABLE book_copy
-- DROP COLUMN shelf_location;
--
-- -- 3. Đảm bảo cột status đồng nhất với Enum (VARCHAR)
-- ALTER TABLE book_copy
--     MODIFY COLUMN status VARCHAR(50);

ALTER TABLE book_copy
ADD COLUMN shelf_id BIGINT;

ALTER TABLE book_copy
ADD CONSTRAINT fk_copy_shelf
FOREIGN KEY (shelf_id) references shelf(shelf_id);

alter table book_copy
drop column shelf_location;

     alter table book_copy
     modify column status varchar(50);