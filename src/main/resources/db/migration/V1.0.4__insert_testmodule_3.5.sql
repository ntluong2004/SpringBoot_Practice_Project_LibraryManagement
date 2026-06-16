-- 1. Chèn đầu sách để test Đánh giá (Review)
INSERT INTO book (title, author, publisher, isbn, status) VALUES
                                                              ('Lập trình Java Backend với Spring Boot', 'Nguyễn Văn A', 'NXB Công Nghệ', '978-604-123456', 'Available'),
                                                              ('Sạch và Gọn (Clean Code)', 'Robert C. Martin', 'NXB Trẻ', '978-604-987654', 'Available');

-- 2. Chèn bản sao sách để test Đặt trước (Reservation)
-- (Sử dụng book_id 1 và 2 vừa tạo ở trên)
INSERT INTO book_copy (barcode, status, book_id)
SELECT 'BC001', 'Available', book_id FROM book WHERE isbn = '978-604-123456';

INSERT INTO book_copy (barcode, status, book_id)
SELECT 'BC002', 'Available', book_id FROM book WHERE isbn = '978-604-123456';

INSERT INTO book_copy (barcode, status, book_id)
SELECT 'BC003', 'Available', book_id FROM book WHERE isbn = '978-604-987654';