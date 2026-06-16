package com.library.library_manager.controller;

import com.library.library_manager.dto.ApiResponse;
import com.library.library_manager.dto.PageResponse;
import com.library.library_manager.dto.fine.FineRequestDTO;
import com.library.library_manager.dto.fine.FineResponseDTO;
import com.library.library_manager.dto.return_book.*;
import com.library.library_manager.service.IReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final IReturnService returnService;

    @PostMapping("/scan")
    public ApiResponse<ReturnScanResponseDTO> scanReturn(@RequestBody @Valid ReturnScanRequestDTO requestDTO) {
        return ApiResponse.<ReturnScanResponseDTO>builder()
                .data(returnService.scanReturn(requestDTO))
                .build();
    }

    @PostMapping
    public ApiResponse<ReturnTransactionResponseDTO> confirmReturn(@RequestBody @Valid ReturnConfirmRequestDTO requestDTO) {
        return ApiResponse.<ReturnTransactionResponseDTO>builder()
                .data(returnService.confirmReturn(requestDTO))
                .build();
    }

    @PostMapping("/{loanId}/condition-check")
    public ApiResponse<ReturnTransactionResponseDTO> checkCondition(
            @PathVariable Long loanId,
            @RequestBody @Valid ConditionCheckRequestDTO requestDTO) {
        return ApiResponse.<ReturnTransactionResponseDTO>builder()
                .data(returnService.checkCondition(loanId, requestDTO))
                .build();
    }

    @PostMapping("/{loanId}/issues")
    public ApiResponse<FineResponseDTO> reportIssue(
            @PathVariable Long loanId,
            @RequestBody @Valid IssueRequestDTO requestDTO) {
        return ApiResponse.<FineResponseDTO>builder()
                .data(returnService.reportIssue(loanId, requestDTO))
                .build();
    }

    @PatchMapping("/{loanId}/fines/{fineId}")
    public ApiResponse<FineResponseDTO> updateFinesAfterReturn(
            @PathVariable Long loanId,
            @PathVariable Long fineId,
            @RequestBody @Valid FineRequestDTO requestDTO) {
        // Có thể thêm tham số loanId vào DTO nếu cần validation ở tầng dưới
        return ApiResponse.<FineResponseDTO>builder()
                .data(returnService.updateFinesAfterReturn(loanId, fineId, requestDTO))
                .build();
    }

    @PostMapping("/{loanId}/send-receipt")
    public ApiResponse<String> sendReceipt(@PathVariable Long loanId) {
        returnService.sendReceipt(loanId);
        return ApiResponse.<String>builder()
                .data("Gửi biên nhận thành công")
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<ReturnTransactionResponseDTO>> getReturnTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String studentCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        
        return ApiResponse.<PageResponse<ReturnTransactionResponseDTO>>builder()
                .data(returnService.getReturnTransactions(page, size, studentCode, fromDate, toDate))
                .build();
    }

    @GetMapping("/{returnId}")
    public ApiResponse<ReturnTransactionResponseDTO> getReturnDetail(@PathVariable Long returnId) {
        return ApiResponse.<ReturnTransactionResponseDTO>builder()
                .data(returnService.getReturnDetail(returnId))
                .build();
    }
}
