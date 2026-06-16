-- ============================================================
-- V1.0.7: Dữ liệu mẫu — dùng LAST_INSERT_ID() thay SET @var
-- Lý do: Flyway reset biến @var giữa các statement → NULL
-- LAST_INSERT_ID() là connection-safe, không bị reset
-- ============================================================

-- ── 1. ADMIN USER ──
INSERT INTO user (user_name, email, full_name, password, phone_number)
VALUES ('admin01', 'admin@library.com', 'Admin User', '$2a$12$Y/yJ.YvP...Z', '0123456789');

INSERT INTO user_role (user_id, role_id)
SELECT LAST_INSERT_ID(), id FROM role WHERE role_name = 'ADMIN';

-- ── 2. STAFF USER ──
INSERT INTO user (user_name, email, full_name, password, phone_number)
VALUES ('staff01', 'staff@library.com', 'Staff User', '$2a$12$Y/yJ.YvP...Z', '0987654321');

INSERT INTO user_role (user_id, role_id)
SELECT LAST_INSERT_ID(), id FROM role WHERE role_name = 'STAFF';

-- Thêm Position trước (user_role không có AUTO_INCREMENT nên LAST_INSERT_ID() vẫn = staff user_id)
INSERT INTO position (position_name, description) VALUES ('Librarian', 'Quản lý thư viện chính');
-- Bây giờ LAST_INSERT_ID() = position_id → dùng subquery cho staff
INSERT INTO staff (staff_id, position_id)
SELECT u.user_id, p.position_id
FROM user u, position p
WHERE u.user_name = 'staff01' AND p.position_name = 'Librarian';

-- ── 3. STUDENT USER ──
INSERT INTO user (user_name, email, full_name, password, phone_number)
VALUES ('student01', 'student@university.edu', 'Student User', '$2a$12$Y/yJ.YvP...Z', '0922222222');

INSERT INTO user_role (user_id, role_id)
SELECT LAST_INSERT_ID(), id FROM role WHERE role_name = 'STUDENT';

-- user_role không có AUTO_INCREMENT → LAST_INSERT_ID() vẫn = student01's user_id
INSERT INTO student (student_id, student_code, class, major, status, total_debt)
VALUES (LAST_INSERT_ID(), 'SV002', 'KTPM2021', 'Kỹ thuật phần mềm', 'ACTIVE', 0.0);

-- ── 4. CATEGORY ──
INSERT INTO category (category_name, description) VALUES
('Khoa học máy tính', 'Sách về công nghệ thông tin và khoa học máy tính'),
('Kinh tế', 'Sách về kinh tế, tài chính');

-- ── 5. BOOKS ──
INSERT INTO book (title, author, publisher, isbn, price, description, status) VALUES
('Cấu trúc dữ liệu và giải thuật', 'Nguyễn Văn B', 'NXB Giáo Dục', '978-604-111111', 150000, 'Sách cơ bản về CTDL', 'Available'),
('Kinh tế vĩ mô', 'Trần Văn C', 'NXB Kinh Tế', '978-604-222222', 200000, 'Sách kinh tế đại cương', 'Available');

-- ── 6. BOOK_CATEGORY ──
INSERT INTO book_category (book_id, category_id)
SELECT b.book_id, c.category_id FROM book b, category c
WHERE b.isbn = '978-604-111111' AND c.category_name = 'Khoa học máy tính';

INSERT INTO book_category (book_id, category_id)
SELECT b.book_id, c.category_id FROM book b, category c
WHERE b.isbn = '978-604-222222' AND c.category_name = 'Kinh tế';

-- ── 7. SHELF ──
INSERT INTO shelf (shelf_code, area_zone, floor_level, book_id)
SELECT 'A1-01', 'Zone A', 1, book_id FROM book WHERE isbn = '978-604-111111';

INSERT INTO shelf (shelf_code, area_zone, floor_level, book_id)
SELECT 'B2-02', 'Zone B', 2, book_id FROM book WHERE isbn = '978-604-222222';

-- ── 8. BOOK COPIES ──
INSERT INTO book_copy (book_id, barcode, status, shelf_location)
SELECT book_id, 'BC1001', 'AVAILABLE', 'A1-01' FROM book WHERE isbn = '978-604-111111';

INSERT INTO book_copy (book_id, barcode, status, shelf_location)
SELECT book_id, 'BC1002', 'BORROWED', 'A1-01' FROM book WHERE isbn = '978-604-111111';

INSERT INTO book_copy (book_id, barcode, status, shelf_location)
SELECT book_id, 'BC1003', 'AVAILABLE', 'B2-02' FROM book WHERE isbn = '978-604-222222';

-- ── 9. LOAN (dùng subquery trong SELECT — KHÔNG dùng VALUES) ──
INSERT INTO loan (borrow_date, due_date, status, user_id, copy_id, staff_id)
SELECT NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'BORROWED',
       u.user_id, c.copy_id, s.staff_id
FROM user u, book_copy c, staff s
WHERE u.user_name = 'student01'
  AND c.barcode = 'BC1002'
  AND s.staff_id = (SELECT user_id FROM user WHERE user_name = 'staff01');

-- ── 10. RESERVATION ──
INSERT INTO reservation (student_id, book_id, request_date, status)
SELECT u.user_id, b.book_id, NOW(), 'PENDING'
FROM user u, book b
WHERE u.user_name = 'student01' AND b.isbn = '978-604-222222';

-- ── 11. BOOK REVIEW ──
INSERT INTO book_review (book_id, student_id, rating, comment, created_at)
SELECT b.book_id, u.user_id, 5, 'Sách rất hay và hữu ích!', NOW()
FROM book b, user u
WHERE b.isbn = '978-604-111111' AND u.user_name = 'student01';

-- ── 12. NOTIFICATION ──
INSERT INTO notification (user_id, title, content, type, is_read, created_at)
SELECT user_id, 'Chào mừng', 'Chào mừng bạn đến với hệ thống thư viện', 'SYSTEM', b'0', NOW()
FROM user WHERE user_name = 'student01';
