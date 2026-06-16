package com.library.library_manager.service.impl;

import com.library.library_manager.dto.PageResponse;
import com.library.library_manager.dto.fine.FineRequestDTO;
import com.library.library_manager.dto.fine.FineResponseDTO;
import com.library.library_manager.dto.return_book.*;
import com.library.library_manager.entity.*;
import com.library.library_manager.enums.BookCopyStatus;
import com.library.library_manager.exception.AppException;
import com.library.library_manager.exception.ErrorCode;
import com.library.library_manager.mapper.ReturnMapper;
import com.library.library_manager.repository.*;
import com.library.library_manager.service.IFineService;
import com.library.library_manager.service.IReturnService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturnServiceImpl implements IReturnService {

    ILoanRepository loanRepository;
    IReturnTransactionRepository returnRepository;
    IBookCopyRepository bookCopyRepository;
    IStaffRepository staffRepository;
    IFineService fineService;
    ReturnMapper returnMapper;

    @Override
    public ReturnScanResponseDTO scanReturn(ReturnScanRequestDTO requestDTO) {
        // Tìm loan đang mượn
        Loan loan = loanRepository.findByUser_Student_StudentCodeAndBookCopy_BarcodeAndReturnedAtIsNull(
                        requestDTO.getStudentCode(),
                        requestDTO.getBarcode()
                )
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn tương ứng với barcode này."));

        long overdueDays = 0;
        double estimatedLateFee = 0.0;

        if (LocalDateTime.now().isAfter(loan.getDueDate())) {
            overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDateTime.now());
            // Tính phí trễ hạn ước tính, ví dụ: 5000/ngày
            estimatedLateFee = overdueDays * SystemRule.LATE_FEE_PER_DAY.asDouble();
        }

        return ReturnScanResponseDTO.builder()
                .loanId(loan.getId())
                .bookTitle(loan.getBookCopy().getBook().getTitle())
                .barcode(loan.getBookCopy().getBarcode())
                .studentCode(loan.getUser().getStudent().getStudentCode())
                .studentName(loan.getUser().getFullName())
                .borrowDate(loan.getBorrowDate())
                .dueDate(loan.getDueDate())
                .overdueDays(overdueDays)
                .estimatedLateFee(estimatedLateFee)
                .build();
    }

    @Override
    @Transactional
    public ReturnTransactionResponseDTO confirmReturn(ReturnConfirmRequestDTO requestDTO) {
        Loan loan = loanRepository.findById(requestDTO.getLoanId())
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

        if (loan.getReturnedAt() != null) {
            throw new AppException(ErrorCode.LOAN_ALREADY_CONFIRMED);
        }

        Staff staff = null;
        if (requestDTO.getStaffId() != null) {
            staff = staffRepository.findById(requestDTO.getStaffId()).orElse(null);
        }

        // Cập nhật Loan
        loan.setStatus("RETURNED");
        loan.setReturnedAt(LocalDateTime.now());
        loan.setReturnStatus(requestDTO.getCondition() != null ? requestDTO.getCondition() : "Bình thường");
        loanRepository.save(loan);

        // Mở khóa BookCopy
        BookCopy copy = loan.getBookCopy();
        copy.setIsLocked(false);
        copy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);

        // Tính toán trễ hạn
        long overdueDays = 0;
        double lateFee = 0.0;
        if (LocalDateTime.now().isAfter(loan.getDueDate())) {
            overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDateTime.now());
            lateFee = overdueDays * SystemRule.LATE_FEE_PER_DAY.asDouble();
        }

        // Tạo ReturnTransaction
        ReturnTransaction returnTransaction = ReturnTransaction.builder()
                .loan(loan)
                .staff(staff)
                .returnDate(LocalDateTime.now())
                .actualCondition(requestDTO.getCondition() != null ? requestDTO.getCondition() : "Bình thường")
                .lateFee(lateFee)
                .build();
        
        ReturnTransaction savedTransaction = returnRepository.save(returnTransaction);

        // Nếu có trễ hạn, tạo Violation (Phí phạt) tự động
        if (lateFee > 0) {
            FineRequestDTO fineRequest = FineRequestDTO.builder()
                    .studentCode(loan.getUser().getStudent().getStudentCode())
                    .type("LATE_RETURN")
                    .fineAmount(lateFee)
                    .notes("Trễ hạn " + overdueDays + " ngày")
                    .dueDate(LocalDateTime.now().plusDays(7)) // Hạn nộp phạt là 7 ngày sau khi trả
                    .loanId(loan.getId())
                    .build();
            fineService.createFine(fineRequest);
        }

        return returnMapper.returnToReturnTransactionResponseDTO(savedTransaction);
    }

    @Override
    @Transactional
    public ReturnTransactionResponseDTO checkCondition(Long loanId, ConditionCheckRequestDTO requestDTO) {
        // Tìm ReturnTransaction thông qua loanId
        // Lưu ý: Tạm thời tìm bằng query tự viết hoặc duyệt, giả sử chỉ có 1 ReturnTransaction cho 1 Loan
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
        
        ReturnTransaction returnTransaction = returnRepository.findAll().stream()
                .filter(rt -> rt.getLoan().getId().equals(loanId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Chưa tìm thấy giao dịch trả sách cho phiếu mượn này."));

        returnTransaction.setActualCondition(requestDTO.getCondition());
        loan.setReturnStatus(requestDTO.getCondition());
        
        loanRepository.save(loan);
        return returnMapper.returnToReturnTransactionResponseDTO(returnRepository.save(returnTransaction));
    }

    @Override
    @Transactional
    public FineResponseDTO reportIssue(Long loanId, IssueRequestDTO requestDTO) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

        // Cập nhật trạng thái sách
        BookCopy copy = loan.getBookCopy();
        if ("LOAN_DAMAGED".equals(requestDTO.getType())) {
            copy.setStatus(BookCopyStatus.DAMAGED);
        } else if ("LOAN_LOST".equals(requestDTO.getType())) {
            copy.setStatus(BookCopyStatus.LOST);
        }
        copy.setIsLocked(true); // Khóa sách lại để đem đi sửa hoặc xác nhận mất
        bookCopyRepository.save(copy);

        // Tạo Fine
        FineRequestDTO fineRequest = FineRequestDTO.builder()
                .studentCode(loan.getUser().getStudent().getStudentCode())
                .type(requestDTO.getType())
                .fineAmount(requestDTO.getFineAmount())
                .notes(requestDTO.getNotes())
                .dueDate(LocalDateTime.now().plusDays(14))
                .loanId(loanId)
                .build();

        return fineService.createFine(fineRequest);
    }

    @Override
    @Transactional
    public FineResponseDTO updateFinesAfterReturn(Long loanId, Long fineId, FineRequestDTO requestDTO) {
        // Có thể bổ sung check loanId có khớp fineId không nếu cần thiết
        return fineService.updateFine(fineId, requestDTO);
    }

    @Override
    public void sendReceipt(Long loanId) {
        System.out.println("Mock: Đã gửi biên nhận trả sách cho phiếu mượn #" + loanId);
    }

    @Override
    public PageResponse<ReturnTransactionResponseDTO> getReturnTransactions(
            int page, int size, String studentCode, LocalDateTime fromDate, LocalDateTime toDate) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<ReturnTransaction> pageData = returnRepository.findReturnsWithFilters(studentCode, fromDate, toDate, pageable);
        return new PageResponse<>(pageData.map(returnMapper::returnToReturnTransactionResponseDTO));
    }

    @Override
    public ReturnTransactionResponseDTO getReturnDetail(Long returnId) {
        ReturnTransaction rt = returnRepository.findById(returnId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch trả sách."));
        return returnMapper.returnToReturnTransactionResponseDTO(rt);
    }
}
