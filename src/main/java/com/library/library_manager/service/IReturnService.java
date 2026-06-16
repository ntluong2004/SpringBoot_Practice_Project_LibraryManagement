package com.library.library_manager.service;

import com.library.library_manager.dto.PageResponse;
import com.library.library_manager.dto.fine.FineRequestDTO;
import com.library.library_manager.dto.fine.FineResponseDTO;
import com.library.library_manager.dto.return_book.*;

import java.time.LocalDateTime;

public interface IReturnService {
    ReturnScanResponseDTO scanReturn(ReturnScanRequestDTO requestDTO);
    
    ReturnTransactionResponseDTO confirmReturn(ReturnConfirmRequestDTO requestDTO);
    
    ReturnTransactionResponseDTO checkCondition(Long loanId, ConditionCheckRequestDTO requestDTO);
    
    FineResponseDTO reportIssue(Long loanId, IssueRequestDTO requestDTO);
    
    FineResponseDTO updateFinesAfterReturn(Long loanId, Long fineId, FineRequestDTO requestDTO);
    
    void sendReceipt(Long loanId);
    
    PageResponse<ReturnTransactionResponseDTO> getReturnTransactions(
            int page, int size, String studentCode, LocalDateTime fromDate, LocalDateTime toDate);
            
    ReturnTransactionResponseDTO getReturnDetail(Long returnId);
}
