-- Cập nhật bảng violation để hỗ trợ phí phạt độc lập với loan (phí phạt thủ công)
ALTER TABLE violation
ADD COLUMN student_id BIGINT,
ADD COLUMN notes VARCHAR(255),
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN due_date TIMESTAMP,
ADD COLUMN status VARCHAR(50) DEFAULT 'UNPAID';

-- Thêm khóa ngoại cho student_id
ALTER TABLE violation
ADD CONSTRAINT fk_violation_student
FOREIGN KEY (student_id) REFERENCES student(student_id);

-- Migration dữ liệu cũ:
-- 1. Với các violation đang có loan_id, cập nhật student_id từ bảng loan -> user -> student
UPDATE violation v
SET student_id = (
    SELECT l.user_id 
    FROM loan l 
    WHERE l.loan_id = v.loan_id
)
WHERE v.loan_id IS NOT NULL;

-- 2. Cập nhật status dựa trên is_paid
UPDATE violation
SET status = CASE 
    WHEN is_paid = true THEN 'PAID'
    ELSE 'UNPAID'
END;
