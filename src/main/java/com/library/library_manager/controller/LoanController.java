package com.library.library_manager.controller;

import com.library.library_manager.dto.*;
import com.library.library_manager.service.impl.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;


    /**
     * POST /api/loans/check-eligibility
     * Kiểm tra sinh viên có đủ điều kiện mượn cuốn sách (barcode) không
     * Body: { "studentCode": "SV001", "copyId": 1 }
     */
    @PostMapping("/loans/check-eligibility")
    public ResponseEntity<ApiResponse<CheckEligibilityResponseDTO>> checkEligibility(
            @RequestBody CheckRequestDTO dto) {
        CheckEligibilityResponseDTO result = loanService.checkEligibility(dto.getStudentCode(), dto.getCopyId());
        return ResponseEntity.ok(ApiResponse.<CheckEligibilityResponseDTO>builder()
                .message(result.isEligible() ? "Đủ điều kiện mượn sách." : "Không đủ điều kiện mượn sách.")
                .data(result)
                .build());
    }

    /**
     * POST /api/loans
     * Tạo phiếu mượn mới (mượn tự do tại quầy, không qua đặt trước)
     * Body: { "studentCode": "SV001", "copyId": 1, "dueDate": "...", "note": "..." }
     */
    @PostMapping("/loans")
    public ResponseEntity<ApiResponse<LoanResponseDTO>> createLoan(
            @RequestBody LoanRequestDTO dto) {
        LoanResponseDTO loan = loanService.createLoan(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<LoanResponseDTO>builder()
                        .message("Phiếu mượn đã được tạo thành công. Chờ xác nhận giao sách.")
                        .data(loan)
                        .build());
    }

    /**
     * POST /api/loans/{loanId}/confirm-pickup
     * Xác nhận đã giao sách tận tay → đồng hồ hạn trả bắt đầu chạy
     */
    @PostMapping("/loans/{loanId}/confirm-pickup")
    public ResponseEntity<ApiResponse<LoanResponseDTO>> confirmPickup(
            @PathVariable Long loanId) {
        LoanResponseDTO loan = loanService.confirmPickup(loanId);
        return ResponseEntity.ok(ApiResponse.<LoanResponseDTO>builder()
                .message("Đã xác nhận giao sách. Hạn trả: " + loan.getDueDate())
                .data(loan)
                .build());
    }

    /**
     * GET /api/loans
     * Liệt kê phiếu mượn, hỗ trợ lọc theo studentCode và/hoặc status
     */
    @GetMapping("/loans")
    public ResponseEntity<ApiResponse<List<LoanResponseDTO>>> getLoans(
            @RequestParam(required = false) String studentCode,
            @RequestParam(required = false) String status) {
        List<LoanResponseDTO> loans = loanService.getLoans(studentCode, status);
        return ResponseEntity.ok(ApiResponse.<List<LoanResponseDTO>>builder()
                .data(loans)
                .build());
    }

    /**
     * GET /api/loans/{loanId}
     * Xem chi tiết một phiếu mượn
     */
    @GetMapping("/loans/{loanId}")
    public ResponseEntity<ApiResponse<LoanResponseDTO>> getLoanDetail(
            @PathVariable Long loanId) {
        return ResponseEntity.ok(ApiResponse.<LoanResponseDTO>builder()
                .data(loanService.getLoanDetail(loanId))
                .build());
    }

    /**
     * DELETE /api/loans/{loanId}
     * Hủy phiếu mượn trước khi giao sách; mở lại bản in
     */
    @DeleteMapping("/loans/{loanId}")
    public ResponseEntity<ApiResponse<Void>> cancelLoan(
            @PathVariable Long loanId) {
        loanService.cancelLoanBeforePickup(loanId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Đã hủy phiếu mượn và mở lại bản in.")
                .build());
    }

    /**
     * PATCH /api/loans/{loanId}/mark-issue
     * Đánh dấu sách bị mất hoặc hư hỏng trong quá trình mượn
     * Param: type = LOST | DAMAGED
     */
    @PatchMapping("/loans/{loanId}/mark-issue")
    public ResponseEntity<ApiResponse<LoanResponseDTO>> markIssue(
            @PathVariable Long loanId,
            @RequestParam String type) {
        LoanResponseDTO result = loanService.markIssue(loanId, type);
        return ResponseEntity.ok(ApiResponse.<LoanResponseDTO>builder()
                .message("Đã cập nhật trạng thái: " + type + ". Tiền phạt đã được ghi nhận vào công nợ.")
                .data(result)
                .build());
    }
}