package com.library.library_manager.controller;

import com.library.library_manager.dto.ApiResponse;
import com.library.library_manager.dto.PageResponse;
import com.library.library_manager.dto.fine.*;
import com.library.library_manager.service.IFineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FineController {

    private final IFineService fineService;

    // --- FINES ---
    
    @GetMapping("/fines")
    public ApiResponse<PageResponse<FineResponseDTO>> getFines(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String studentCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        return ApiResponse.<PageResponse<FineResponseDTO>>builder()
                .data(fineService.getFines(page, size, studentCode, status, type))
                .build();
    }

    @GetMapping("/fines/{fineId}")
    public ApiResponse<FineResponseDTO> getFineDetail(@PathVariable Long fineId) {
        return ApiResponse.<FineResponseDTO>builder()
                .data(fineService.getFineDetail(fineId))
                .build();
    }

    @PostMapping("/fines")
    public ApiResponse<FineResponseDTO> createFine(@RequestBody @Valid FineRequestDTO requestDTO) {
        return ApiResponse.<FineResponseDTO>builder()
                .data(fineService.createFine(requestDTO))
                .build();
    }

    @PutMapping("/fines/{fineId}")
    public ApiResponse<FineResponseDTO> updateFine(
            @PathVariable Long fineId,
            @RequestBody @Valid FineRequestDTO requestDTO) {
        return ApiResponse.<FineResponseDTO>builder()
                .data(fineService.updateFine(fineId, requestDTO))
                .build();
    }

    @DeleteMapping("/fines/{fineId}")
    public ApiResponse<String> deleteFine(@PathVariable Long fineId) {
        fineService.deleteFine(fineId);
        return ApiResponse.<String>builder()
                .data("Deleted fine successfully")
                .build();
    }

    // --- PAYMENTS ---
    
    @PostMapping("/fines/{fineId}/payments")
    public ApiResponse<FinePaymentResponseDTO> recordPayment(
            @PathVariable Long fineId,
            @RequestBody @Valid FinePaymentRequestDTO requestDTO) {
        return ApiResponse.<FinePaymentResponseDTO>builder()
                .data(fineService.recordPayment(fineId, requestDTO))
                .build();
    }

    @GetMapping("/fine-payments")
    public ApiResponse<PageResponse<FinePaymentResponseDTO>> getFinePayments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String studentCode,
            @RequestParam(required = false) String method) {
        return ApiResponse.<PageResponse<FinePaymentResponseDTO>>builder()
                .data(fineService.getFinePayments(page, size, studentCode, method))
                .build();
    }

    @GetMapping("/fines/{fineId}/payments")
    public ApiResponse<List<FinePaymentResponseDTO>> getPaymentHistoryForFine(@PathVariable Long fineId) {
        return ApiResponse.<List<FinePaymentResponseDTO>>builder()
                .data(fineService.getPaymentHistoryForFine(fineId))
                .build();
    }

    @PostMapping("/fines/{fineId}/send-receipt")
    public ApiResponse<String> sendReceipt(@PathVariable Long fineId) {
        fineService.sendReceipt(fineId);
        return ApiResponse.<String>builder()
                .data("Receipt sent successfully")
                .build();
    }
}
