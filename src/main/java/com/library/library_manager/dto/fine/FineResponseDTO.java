package com.library.library_manager.dto.fine;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineResponseDTO {
    Long id;
    String type;
    Double fineAmount;
    Boolean isPaid; // Giữ lại cho tương thích cũ
    String status;  // UNPAID, PARTIAL, PAID, CANCELLED
    String notes;
    LocalDateTime createdAt;
    LocalDateTime dueDate;
    
    // Thông tin sinh viên
    Long studentId;
    String studentCode;
    String studentName;

    // Thông tin phiếu mượn (nếu có)
    Long loanId;
    String bookTitle;
}
