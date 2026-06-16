package com.library.library_manager.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowSessionResponseDTO {
    // Thông tin sinh viên
    String studentName;
    String studentCode;
    // Danh sách đơn đặt trước đang chờ (PENDING)
    List<ReservationResponseDTO> pendingReservations;
    // Danh sách sách đang mượn (chưa trả)
    List<LoanResponseDTO> currentLoans;
    // Công nợ và hạn mức
    Double totalDebt;
    Long borrowedCount;    // số đang mượn/đặt
    Long remainingLimit;   // số còn được mượn thêm
}