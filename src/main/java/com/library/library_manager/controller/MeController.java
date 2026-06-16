package com.library.library_manager.controller;

import com.library.library_manager.dto.*;
import com.library.library_manager.dto.student.StudentProfileResponseDTO;
import com.library.library_manager.entity.Notification;
import com.library.library_manager.service.impl.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    // Hãy đảm bảo có chữ 'final' để @RequiredArgsConstructor hoạt động đúng
    private final StudentService studentService;

    private final String CURRENT_USER = "SV001"; // Giả lập username để test

    // 1. Xem hồ sơ cá nhân
    @GetMapping("/profile")
    public ResponseEntity<StudentProfileResponseDTO> getProfile() {
        return ResponseEntity.ok(studentService.getProfileByUsername(CURRENT_USER));
    }

    // 2. Xem sách đang mượn
    @GetMapping("/borrowed-items")
    public ResponseEntity<List<BorrowedItemResponse>> getBorrowedItems() {
        return ResponseEntity.ok(studentService.getBorrowedItems(CURRENT_USER));
    }

    // 3. Xem lịch sử mượn trả
    @GetMapping("/borrow-history")
    public ResponseEntity<List<HistoryResponse>> getBorrowHistory() {
        return ResponseEntity.ok(studentService.getBorrowHistory(CURRENT_USER));
    }

    // 4. Xem đặt trước hiện có
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponseDTO>> getReservations() {
        return ResponseEntity.ok(studentService.getReservations(CURRENT_USER));
    }

    // 5. Xem vi phạm (Chi tiết từng lỗi)
    @GetMapping("/violations")
    public ResponseEntity<List<ViolationResponse>> getViolations() {
        return ResponseEntity.ok(studentService.getViolations(CURRENT_USER));
    }

    // 6. Xem tổng tiền phạt cần đóng
    @GetMapping("/fines")
    public ResponseEntity<FineResponse> getFines() {
        return ResponseEntity.ok(studentService.getTotalFines(CURRENT_USER));
    }

    // 7. Xem lịch sử thanh toán
    @GetMapping("/fine-payments")
    public ResponseEntity<List<PaymentHistoryResponse>> getPaymentHistory() {
        return ResponseEntity.ok(studentService.getPaymentHistory(CURRENT_USER));
    }

    // 8. Nhận thông báo
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications() {
        return ResponseEntity.ok(studentService.getNotifications(CURRENT_USER));
    }
}
