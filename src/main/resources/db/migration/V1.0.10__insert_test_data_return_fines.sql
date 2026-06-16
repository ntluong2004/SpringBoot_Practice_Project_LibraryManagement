-- ============================================================
-- V1.0.10: Dữ liệu mẫu test API Trả Sách & Phí Phạt
-- ============================================================

-- ── 1. THÊM SINH VIÊN MỚI ──
INSERT INTO user (user_name, email, full_name, password, phone_number)
VALUES ('student02', 'sv003@university.edu', 'Nguyễn Văn Test', '$2a$12$Y/yJ.YvP...Z', '0933333333');

INSERT INTO user_role (user_id, role_id)
SELECT LAST_INSERT_ID(), id FROM role WHERE role_name = 'STUDENT';

INSERT INTO student (student_id, student_code, class, major, status, total_debt)
VALUES (LAST_INSERT_ID(), 'SV003', 'KTPM2021', 'Kỹ thuật phần mềm', 'ACTIVE', 50000.0);

-- ── 2. THÊM SÁCH & BẢN SAO ──
INSERT INTO book (title, author, publisher, isbn, price, description, status) VALUES
('Clean Code', 'Robert C. Martin', 'NXB Trẻ', '978-604-333333', 300000, 'Sách lập trình', 'Available'),
('Design Patterns', 'GoF', 'Addison-Wesley', '978-604-444444', 400000, 'Sách thiết kế', 'Available');

INSERT INTO book_copy (book_id, barcode, status, shelf_id)
SELECT book_id, 'BC1004', 'BORROWED', '1' FROM book WHERE isbn = '978-604-333333';

INSERT INTO book_copy (book_id, barcode, status, shelf_id)
SELECT book_id, 'BC1005', 'BORROWED', '2' FROM book WHERE isbn = '978-604-444444';

INSERT INTO book_copy (book_id, barcode, status, shelf_id)
SELECT book_id, 'BC1006', 'AVAILABLE', '1' FROM book WHERE isbn = '978-604-444444';

-- ── 3. THÊM PHIẾU MƯỢN (LOAN) ──

-- Phiếu mượn 1: Trễ hạn (Của SV002 - student01)
INSERT INTO loan (borrow_date, due_date, status, user_id, copy_id, staff_id)
SELECT DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), 'BORROWED',
       u.user_id, c.copy_id, s.staff_id
FROM user u, book_copy c, staff s
WHERE u.user_name = 'student01'
  AND c.barcode = 'BC1004'
  AND s.staff_id = (SELECT user_id FROM user WHERE user_name = 'staff01');

-- Phiếu mượn 2: Chưa đến hạn (Của SV003 - student02)
INSERT INTO loan (borrow_date, due_date, status, user_id, copy_id, staff_id)
SELECT NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'BORROWED',
       u.user_id, c.copy_id, s.staff_id
FROM user u, book_copy c, staff s
WHERE u.user_name = 'student02'
  AND c.barcode = 'BC1005'
  AND s.staff_id = (SELECT user_id FROM user WHERE user_name = 'staff01');

-- ── 4. THÊM KHOẢN PHẠT (VIOLATION) ──

-- Khoản phạt 1: Phạt thủ công cho SV003
INSERT INTO violation (student_id, type, fine_amount, notes, status, created_at, due_date, is_paid)
SELECT student_id, 'OTHER', 50000, 'Phí làm lại thẻ thư viện', 'UNPAID', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), false
FROM student WHERE student_code = 'SV003';

-- Khoản phạt 2: Phạt hư hỏng sách cho SV002 (Đã thanh toán một phần)
INSERT INTO violation (student_id, loan_id, type, fine_amount, notes, status, created_at, due_date, is_paid)
SELECT u.user_id, l.loan_id, 'DAMAGED_BOOK', 100000, 'Làm rách bìa sách', 'PARTIAL', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), false
FROM user u
JOIN loan l ON l.user_id = u.user_id
JOIN book_copy c ON l.copy_id = c.copy_id
WHERE u.user_name = 'student01' AND c.barcode = 'BC1002';

-- Cập nhật tổng nợ cho SV002 (Do có phạt 100k, đã đóng 30k -> Còn nợ 70k)
UPDATE student SET total_debt = 70000 WHERE student_code = 'SV002';

-- ── 5. THÊM LỊCH SỬ THANH TOÁN (FINE PAYMENT) ──
INSERT INTO fine_payment (amount_paid, payment_date, payment_method, student_id, violation_id)
SELECT 30000, NOW(), 'Tiền mặt', v.student_id, v.violation_id
FROM violation v
WHERE v.type = 'DAMAGED_BOOK' AND v.fine_amount = 100000;
