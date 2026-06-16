package com.library.library_manager.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanResponseDTO {
    Long loanId;
    // Thông tin sinh viên mượn
    String borrowerName;
    String studentCode;
    // Thông tin sách
    String bookTitle;
    String isbn;
    String barcode;
    Long copyId;
    // Thông tin phiếu mượn
    LocalDateTime loanDate;
    LocalDateTime dueDate;
    LocalDateTime returnedAt;
    String status;
    String staffNote;
}
