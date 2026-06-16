-- Chèn dữ liệu mẫu ban đầu
INSERT INTO role (role_name) VALUES ('ADMIN'), ('STAFF'), ('STUDENT');

-- Insert user SV001 → dùng LAST_INSERT_ID() NGAY sau INSERT
INSERT INTO user (user_name, password, full_name, email, phone_number)
VALUES ('SV001', '123456', 'Nguyen Tien Luong', 'luong.student@example.com', '0912345678');

-- Gán Role (LAST_INSERT_ID() = user_id của SV001 vừa tạo)
INSERT INTO user_role (user_id, role_id)
SELECT LAST_INSERT_ID(), id FROM role WHERE role_name = 'STUDENT';

-- Chèn Profile Student (LAST_INSERT_ID() vẫn = user_id vì user_role không có AUTO_INCREMENT)
INSERT INTO student (student_id, student_code, major, class, dob, status, total_debt)
VALUES (LAST_INSERT_ID(), 'SV001', 'Software Engineering', 'SE1601', '2002-05-20', 'ACTIVE', 0.0);