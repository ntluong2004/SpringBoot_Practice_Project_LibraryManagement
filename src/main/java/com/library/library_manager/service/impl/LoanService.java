package com.library.library_manager.service.impl;

import com.library.library_manager.dto.*;
import com.library.library_manager.entity.*;
import com.library.library_manager.enums.BookCopyStatus;
import com.library.library_manager.exception.AppException;
import com.library.library_manager.exception.ErrorCode;
import com.library.library_manager.mapper.LoanMapper;
import com.library.library_manager.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoanService {

    ILoanRepository loanRepository;
    IReservationRepository reservationRepository;
    IStudentRepository studentRepository;
    IBookCopyRepository bookCopyRepository;
    LoanMapper loanMapper;

    // GET /api/students/{studentId}/borrow-session
    public BorrowSessionResponseDTO getBorrowSessionByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        // Lấy các đơn PENDING thông qua studentId
        List<Reservation> pendingRes = reservationRepository.findByStudent_IdAndStatus(studentId, "PENDING");
        // Lấy các phiếu mượn đang active (chưa trả) — student.id trùng với user.userId do @MapsId
        List<Loan> activeLoans = loanRepository.findByUser_UserIdAndReturnedAtIsNull(studentId);

        long borrowedCount = pendingRes.size() + activeLoans.size();
        long remainingLimit = Math.max(0, 5 - borrowedCount);

        return BorrowSessionResponseDTO.builder()
                .studentName(student.getUser().getFullName())
                .studentCode(student.getStudentCode())
                .pendingReservations(loanMapper.toReservationResponseList(pendingRes))
                .currentLoans(loanMapper.toLoansResponseList(activeLoans))
                .totalDebt(student.getTotalDebt())
                .borrowedCount(borrowedCount)
                .remainingLimit(remainingLimit)
                .build();
    }

    // POST /api/loans/check-eligibility
    public CheckEligibilityResponseDTO checkEligibility(String studentCode, Long copyId) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        BookCopy copy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new AppException(ErrorCode.COPY_NOT_FOUND));

        // Kiểm tra công nợ
        if (student.getTotalDebt() != null && student.getTotalDebt() > 0) {
            return CheckEligibilityResponseDTO.builder()
                    .eligible(false)
                    .reason("Sinh viên đang có công nợ chưa thanh toán: " + student.getTotalDebt() + " VNĐ")
                    .studentName(student.getUser().getFullName())
                    .totalDebt(student.getTotalDebt())
                    .copyStatus(copy.getStatus() != null ? copy.getStatus().name() : null)
                    .bookTitle(copy.getBook().getTitle())
                    .build();
        }

        // Kiểm tra tình trạng bản in (phải AVAILABLE và không bị khóa)
        if (Boolean.TRUE.equals(copy.getIsLocked()) || copy.getStatus() != BookCopyStatus.AVAILABLE) {
            return CheckEligibilityResponseDTO.builder()
                    .eligible(false)
                    .reason("Bản in này không sẵn sàng. Trạng thái hiện tại: " + copy.getStatus())
                    .studentName(student.getUser().getFullName())
                    .totalDebt(student.getTotalDebt())
                    .copyStatus(copy.getStatus() != null ? copy.getStatus().name() : null)
                    .bookTitle(copy.getBook().getTitle())
                    .build();
        }

        // Kiểm tra hạn mức mượn (tối đa 5 cuốn)
        // Student.id == User.userId do @MapsId, nên dùng chung để query
        long activeLoans = loanRepository.countByUser_UserNameAndReturnedAtIsNull(student.getUser().getUserName());
        long pendingRes = reservationRepository.countByStudent_IdAndStatus(student.getId(), "PENDING");
        long currentTotal = activeLoans + pendingRes;

        if (currentTotal >= 5) {
            return CheckEligibilityResponseDTO.builder()
                    .eligible(false)
                    .reason("Sinh viên đã đạt hạn mức tối đa 5 cuốn sách.")
                    .studentName(student.getUser().getFullName())
                    .totalDebt(student.getTotalDebt())
                    .currentBorrowCount(currentTotal)
                    .remainingLimit(0L)
                    .copyStatus(copy.getStatus().name())
                    .bookTitle(copy.getBook().getTitle())
                    .build();
        }

        return CheckEligibilityResponseDTO.builder()
                .eligible(true)
                .reason("Đủ điều kiện mượn sách.")
                .studentName(student.getUser().getFullName())
                .totalDebt(student.getTotalDebt())
                .currentBorrowCount(currentTotal)
                .remainingLimit(5 - currentTotal)
                .copyStatus(copy.getStatus().name())
                .bookTitle(copy.getBook().getTitle())
                .build();
    }

    // POST /api/loans — Tạo phiếu mượn mới
    @Transactional
    public LoanResponseDTO createLoan(LoanRequestDTO dto) {
        Student student = studentRepository.findByStudentCode(dto.getStudentCode())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        BookCopy copy = bookCopyRepository.findById(dto.getCopyId())
                .orElseThrow(() -> new AppException(ErrorCode.COPY_NOT_FOUND));

        // Validate lại lần cuối
        if (copy.getStatus() != BookCopyStatus.AVAILABLE || Boolean.TRUE.equals(copy.getIsLocked())) {
            throw new AppException(ErrorCode.COPY_ALREADY_BORROWED);
        }

        LocalDateTime dueDate = dto.getDueDate() != null
                ? dto.getDueDate()
                : LocalDateTime.now().plusDays(14);

        Loan loan = Loan.builder()
                .user(student.getUser())
                .bookCopy(copy)
                .borrowDate(LocalDateTime.now())
                .dueDate(dueDate)
                .status("PENDING_PICKUP")
                .staffNote(dto.getNote())
                .build();

        // Khóa bản in lại
        copy.setIsLocked(true);
        copy.setStatus(BookCopyStatus.RESERVED);
        bookCopyRepository.save(copy);

        Loan saved = loanRepository.save(loan);
        return loanMapper.loanToLoanResponseDTO(saved);
    }

    // POST /api/loans/{loanId}/confirm-pickup — Xác nhận giao sách
    @Transactional
    public LoanResponseDTO confirmPickup(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

        if ("BORROWED".equals(loan.getStatus())) {
            throw new AppException(ErrorCode.LOAN_ALREADY_CONFIRMED);
        }

        // Đồng hồ hạn trả bắt đầu từ lúc giao sách tận tay
        loan.setStatus("BORROWED");
        loan.setBorrowDate(LocalDateTime.now());
        loan.getBookCopy().setStatus(BookCopyStatus.BORROWED);
        bookCopyRepository.save(loan.getBookCopy());
        return loanMapper.loanToLoanResponseDTO(loanRepository.save(loan));
    }

    // POST /api/reservations/{reservationId}/confirm-pickup
    @Transactional
    public LoanResponseDTO confirmPickupFromReservation(Long resId) {
        Reservation res = reservationRepository.findById(resId)
                .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!"PENDING".equals(res.getStatus())) {
            throw new AppException(ErrorCode.RESERVATION_NOT_PENDING);
        }

        Student student = res.getStudent();

        res.setStatus("FULFILLED");
        reservationRepository.save(res);

        Loan loan = Loan.builder()
                .user(student.getUser())
                .bookCopy(res.getBookCopy())
                .borrowDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(14))
                .status("BORROWED")
                .staffNote("Mượn từ đơn đặt trước #" + resId)
                .build();

        if (res.getBookCopy() != null) {
            res.getBookCopy().setStatus(BookCopyStatus.BORROWED);
            bookCopyRepository.save(res.getBookCopy());
        }

        Loan saved = loanRepository.save(loan);
        return loanMapper.loanToLoanResponseDTO(saved);
    }

    // DELETE /api/reservations/{resId} — Hủy đặt trước tại quầy
    @Transactional
    public void cancelReservationAtCounter(Long resId) {
        Reservation res = reservationRepository.findById(resId)
                .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND));

        res.setStatus("CANCELLED");
        if (res.getBookCopy() != null) {
            res.getBookCopy().setIsLocked(false);
            res.getBookCopy().setStatus(BookCopyStatus.AVAILABLE);
            bookCopyRepository.save(res.getBookCopy());
        }
        reservationRepository.save(res);
    }

    // DELETE /api/loans/{loanId} — Hủy phiếu mượn trước khi giao
    @Transactional
    public void cancelLoanBeforePickup(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

        if ("BORROWED".equals(loan.getStatus())) {
            throw new AppException(ErrorCode.LOAN_ALREADY_CONFIRMED);
        }

        BookCopy copy = loan.getBookCopy();
        copy.setIsLocked(false);
        copy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);
        loanRepository.delete(loan);
    }

    // PATCH /api/loans/{loanId}/mark-issue — Đánh dấu mất/hư
    @Transactional
    public LoanResponseDTO markIssue(Long loanId, String issueType) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

        // Cập nhật status phiếu mượn và trạng thái bản in
        loan.setStatus(issueType);
        if ("LOST".equals(issueType)) {
            loan.getBookCopy().setStatus(BookCopyStatus.LOST);
        } else if ("DAMAGED".equals(issueType)) {
            loan.getBookCopy().setStatus(BookCopyStatus.DAMAGED);
        }
        bookCopyRepository.save(loan.getBookCopy());

        // Tính tiền phạt và cộng vào công nợ sinh viên
        Student student = studentRepository.findByUser(loan.getUser())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        double fine = "LOST".equals(issueType) ? 200000.0 : 50000.0;
        student.setTotalDebt(student.getTotalDebt() + fine);
        studentRepository.save(student);

        return loanMapper.loanToLoanResponseDTO(loanRepository.save(loan));
    }

    // GET /api/loans — Lấy danh sách phiếu mượn
    public List<LoanResponseDTO> getLoans(String studentCode, String status) {
        List<Loan> loans;
        if (studentCode != null && status != null) {
            loans = loanRepository.findByStatusAndStudentCode(status, studentCode);
        } else if (studentCode != null) {
            loans = loanRepository.findByUser_Student_StudentCode(studentCode);
        } else if (status != null) {
            loans = loanRepository.findByStatus(status);
        } else {
            loans = loanRepository.findAll();
        }
        return loanMapper.toLoansResponseList(loans);
    }

    // GET /api/loans/{loanId} — Xem chi tiết phiếu mượn
    public LoanResponseDTO getLoanDetail(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
        return loanMapper.loanToLoanResponseDTO(loan);
    }

}
