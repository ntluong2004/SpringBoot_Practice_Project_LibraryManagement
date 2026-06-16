package com.library.library_manager.controller;

import com.library.library_manager.dto.ApiResponse;
import com.library.library_manager.dto.LoanResponseDTO;
import com.library.library_manager.dto.ReservationRequestDTO;
import com.library.library_manager.dto.ReservationResponseDTO;
import com.library.library_manager.service.impl.LoanService;
import com.library.library_manager.service.impl.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final StudentService studentService;
    private final LoanService loanService;
    private final String CURRENT_USER = "SV001";

    // ── Sinh viên: Tạo đặt trước online
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> create(@RequestBody ReservationRequestDTO dto) {
        return ResponseEntity.ok(studentService.createReservation(dto, CURRENT_USER));
    }

    // ── Sinh viên: Xem danh sách đặt trước của mình
    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAll() {
        return ResponseEntity.ok(studentService.getReservations(CURRENT_USER));
    }

    // ── Sinh viên: Xem chi tiết một đặt trước
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDTO> getDetail(@PathVariable Long reservationId) {
        return ResponseEntity.ok(studentService.getReservationDetail(reservationId, CURRENT_USER));
    }

    // ── Sinh viên: Hủy đặt trước online (chưa đến quầy)
    @DeleteMapping("/{reservationId}/cancel")
    public ResponseEntity<String> cancelByStudent(@PathVariable Long reservationId) {
        studentService.cancelReservation(reservationId, CURRENT_USER);
        return ResponseEntity.ok("Hủy đặt trước thành công.");
    }

    // ── Thủ thư: Xác nhận sinh viên đến nhận sách đã đặt → tạo phiếu mượn
    @PostMapping("/{reservationId}/confirm-pickup")
    public ResponseEntity<ApiResponse<LoanResponseDTO>> confirmPickup(@PathVariable Long reservationId) {
        LoanResponseDTO loan = loanService.confirmPickupFromReservation(reservationId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<LoanResponseDTO>builder()
                        .message("Xác nhận đơn đặt trước thành công. Phiếu mượn đã được tạo.")
                        .data(loan)
                        .build());
    }

    // ── Thủ thư: Hủy đặt trước tại quầy (sinh viên từ chối nhận)
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<Void>> cancelAtCounter(@PathVariable Long reservationId) {
        loanService.cancelReservationAtCounter(reservationId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Đã hủy đơn đặt trước và giải phóng bản in.")
                .build());
    }
}