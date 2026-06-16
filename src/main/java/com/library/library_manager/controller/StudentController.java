package com.library.library_manager.controller;

import com.library.library_manager.dto.ApiResponse;
import com.library.library_manager.dto.BorrowHistoryDTO;
import com.library.library_manager.dto.BorrowSessionResponseDTO;
import com.library.library_manager.dto.PasswordResetRequest;
import com.library.library_manager.dto.ViolationDTO;
import com.library.library_manager.dto.student.StudentProfileResponseDTO;
import com.library.library_manager.dto.student.StudentRequestDTO;
import com.library.library_manager.dto.student.StudentResponseDTO;
import com.library.library_manager.entity.Student;
import com.library.library_manager.entity.User;
import com.library.library_manager.repository.IStudentRepository;
import com.library.library_manager.service.IStudentService;
import com.library.library_manager.service.IFineService;
import com.library.library_manager.service.impl.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    private final IStudentService studentService;
    private final IStudentRepository studentRepository;
    private final LoanService loanService;
    private final IFineService fineService;

    @PostMapping
    public ApiResponse<StudentResponseDTO> create(@RequestBody @Valid StudentRequestDTO request) {
        return ApiResponse.<StudentResponseDTO>builder()
                .data(studentService.create(request))
                .build();
    }

    @GetMapping("/{studentId}")
    public ApiResponse<StudentResponseDTO> getDetail(@PathVariable Long studentId) {
        return ApiResponse.<StudentResponseDTO>builder()
                .data(studentService.getById(studentId))
                .build();
    }

    @PatchMapping("/{studentId}/status")
    public ApiResponse<String> updateStatus(
            @PathVariable Long studentId,
            @RequestParam String status) {
        studentService.updateStatus(studentId, status);
        return ApiResponse.<String>builder().data("Update status successfully").build();
    }

    // 6. Đặt lại mật khẩu
    @PostMapping("/{studentId}/reset-password")
    public ResponseEntity<String> resetPassword(
            @PathVariable Long studentId,
            @RequestBody PasswordResetRequest request) {
        studentService.resetPassword(studentId, request.getNewPassword());
        return ResponseEntity.ok("Passworld is changed, student id: " + studentId); //khng cần trả về

    }

    /**
     * GET /api/students/{studentId}/borrow-session
     * Khi quét thẻ sinh viên, trả về tình trạng tổng quát:
     * - danh sách đơn đặt trước đang chờ (PENDING)
     * - sách đang mượn (chưa trả)
     * - công nợ và hạn mức còn lại
     */
    @GetMapping("/{studentId}/borrow-session")
    public ResponseEntity<ApiResponse<BorrowSessionResponseDTO>> getBorrowSession(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.<BorrowSessionResponseDTO>builder()
                .data(loanService.getBorrowSessionByStudentId(studentId))
                .build());
    }

    // 7. Xem lịch sử mượn trả
    @GetMapping("/{studentId}/borrow-history")
    public ResponseEntity<List<BorrowHistoryDTO>> getBorrowHistory(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getBorrowHistory(studentId));
    }

    // 8. Xem lịch sử vi phạm & công nợ
    @GetMapping("/{studentId}/violations")
    public ResponseEntity<List<ViolationDTO>> getViolations(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getViolations(studentId));
    }

    public StudentProfileResponseDTO getProfileByUsername(String username) {
        Student student = studentRepository.findByUser_UserName(username)
                .orElseThrow(() -> new RuntimeException("Don't get student with this username"));

        User user = student.getUser();

        return new StudentProfileResponseDTO(
                user.getFullName(),
                student.getStudentCode(),
                user.getEmail(),
                user.getPhoneNumber(),
                student.getClazz(),
                student.getMajor(),
                student.getStatus()
        );
    }

    @GetMapping("/{studentId}/fine-balance")
    public ApiResponse<com.library.library_manager.dto.fine.FineBalanceResponseDTO> getFineBalance(@PathVariable Long studentId) {
        return ApiResponse.<com.library.library_manager.dto.fine.FineBalanceResponseDTO>builder()
                .data(fineService.getStudentFineBalance(studentId))
                .build();
    }
}
