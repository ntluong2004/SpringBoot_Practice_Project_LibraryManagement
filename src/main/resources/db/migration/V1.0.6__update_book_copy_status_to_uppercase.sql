-- Cập nhật tất cả các trạng thái trong bảng book_copy thành IN HOA
UPDATE book_copy SET status = UPPER(status);
