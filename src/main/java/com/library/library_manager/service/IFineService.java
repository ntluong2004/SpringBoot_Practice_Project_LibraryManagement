package com.library.library_manager.service;

import com.library.library_manager.dto.PageResponse;
import com.library.library_manager.dto.fine.*;

import java.util.List;

public interface IFineService {
    PageResponse<FineResponseDTO> getFines(int page, int size, String studentCode, String status, String type);
    
    FineResponseDTO getFineDetail(Long fineId);
    
    FineResponseDTO createFine(FineRequestDTO requestDTO);
    
    FineResponseDTO updateFine(Long fineId, FineRequestDTO requestDTO);
    
    void deleteFine(Long fineId);
    
    FinePaymentResponseDTO recordPayment(Long fineId, FinePaymentRequestDTO requestDTO);
    
    PageResponse<FinePaymentResponseDTO> getFinePayments(int page, int size, String studentCode, String method);
    
    List<FinePaymentResponseDTO> getPaymentHistoryForFine(Long fineId);
    
    void sendReceipt(Long fineId);
    
    FineBalanceResponseDTO getStudentFineBalance(Long studentId);
}
