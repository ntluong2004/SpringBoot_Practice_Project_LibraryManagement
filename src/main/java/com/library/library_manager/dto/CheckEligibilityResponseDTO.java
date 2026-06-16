package com.library.library_manager.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckEligibilityResponseDTO {
    boolean eligible;  // true = PASS, false = FAIL
    String reason;     // Mô tả lý do pass/fail
    // Thông tin bổ sung cho UI
    String studentName;
    Double totalDebt;
    Long currentBorrowCount;
    Long remainingLimit;
    String copyStatus;
    String bookTitle;
}
