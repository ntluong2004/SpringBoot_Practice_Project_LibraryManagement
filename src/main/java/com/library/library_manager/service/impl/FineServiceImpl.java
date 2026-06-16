package com.library.library_manager.service.impl;

import com.library.library_manager.dto.PageResponse;
import com.library.library_manager.dto.fine.*;
import com.library.library_manager.entity.*;
import com.library.library_manager.exception.AppException;
import com.library.library_manager.exception.ErrorCode;
import com.library.library_manager.mapper.FineMapper;
import com.library.library_manager.repository.*;
import com.library.library_manager.service.IFineService;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FineServiceImpl implements IFineService {

    IViolationRepository violationRepository;
    IFinePaymentRepository finePaymentRepository;
    IStudentRepository studentRepository;
    ILoanRepository loanRepository;
    FineMapper fineMapper;

    @Override
    public PageResponse<FineResponseDTO> getFines(int page, int size, String studentCode, String status, String type) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<Violation> violationPage = violationRepository.findFinesWithFilters(studentCode, status, type, pageable);
        return new PageResponse<>(violationPage.map(fineMapper::violationToFineResponseDTO));
    }

    @Override
    public FineResponseDTO getFineDetail(Long fineId) {
        Violation violation = violationRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));
        return fineMapper.violationToFineResponseDTO(violation);
    }

    @Override
    @Transactional
    public FineResponseDTO createFine(FineRequestDTO requestDTO) {
        Student student = studentRepository.findByStudentCode(requestDTO.getStudentCode())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        Loan loan = null;
        if (requestDTO.getLoanId() != null) {
            loan = loanRepository.findById(requestDTO.getLoanId())
                    .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
        }

        Violation violation = Violation.builder()
                .student(student)
                .loan(loan)
                .type(requestDTO.getType())
                .fineAmount(requestDTO.getFineAmount())
                .notes(requestDTO.getNotes())
                .dueDate(requestDTO.getDueDate())
                .createdAt(LocalDateTime.now())
                .status("UNPAID")
                .isPaid(false)
                .build();

        // Cộng nợ cho sinh viên
        student.setTotalDebt(student.getTotalDebt() + requestDTO.getFineAmount());
        studentRepository.save(student);

        return fineMapper.violationToFineResponseDTO(violationRepository.save(violation));
    }

    @Override
    @Transactional
    public FineResponseDTO updateFine(Long fineId, FineRequestDTO requestDTO) {
        Violation violation = violationRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));

        Double oldAmount = violation.getFineAmount() != null ? violation.getFineAmount() : 0.0;
        Double newAmount = requestDTO.getFineAmount();

        violation.setType(requestDTO.getType());
        violation.setFineAmount(newAmount);
        violation.setNotes(requestDTO.getNotes());
        violation.setDueDate(requestDTO.getDueDate());
        
        // Điều chỉnh lại công nợ sinh viên
        if (!oldAmount.equals(newAmount)) {
            Student student = violation.getStudent();
            if (student == null && violation.getLoan() != null && violation.getLoan().getUser() != null) {
                student = violation.getLoan().getUser().getStudent();
            }
            if (student != null) {
                student.setTotalDebt(student.getTotalDebt() - oldAmount + newAmount);
                studentRepository.save(student);
            }
        }

        return fineMapper.violationToFineResponseDTO(violationRepository.save(violation));
    }

    @Override
    @Transactional
    public void deleteFine(Long fineId) {
        Violation violation = violationRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));

        // Trừ công nợ sinh viên
        Student student = violation.getStudent();
        if (student == null && violation.getLoan() != null && violation.getLoan().getUser() != null) {
            student = violation.getLoan().getUser().getStudent();
        }
        
        if (student != null) {
            // Chỉ trừ đi phần tiền phạt chưa thanh toán (fineAmount - đã thanh toán)
            // Hoặc đơn giản là khi hủy khoản phạt, trả lại số tiền đã đóng hoặc không?
            // Ở đây đơn giản là trừ đi toàn bộ số tiền phạt khỏi totalDebt.
            // Nếu đã thanh toán một phần, thì totalDebt đã bị giảm bởi payment rồi.
            // Số tiền cần trừ = fineAmount - totalPayments
            // Để an toàn, lấy tổng payments cho violation này:
            List<FinePayment> payments = finePaymentRepository.findByViolation_IdOrderByPaymentDateDesc(fineId);
            double totalPaid = payments.stream().mapToDouble(FinePayment::getAmountPaid).sum();
            double unpaidAmount = (violation.getFineAmount() != null ? violation.getFineAmount() : 0.0) - totalPaid;
            
            student.setTotalDebt(Math.max(0, student.getTotalDebt() - unpaidAmount));
            studentRepository.save(student);
        }

        violationRepository.delete(violation);
    }

    @Override
    @Transactional
    public FinePaymentResponseDTO recordPayment(Long fineId, FinePaymentRequestDTO requestDTO) {
        Violation violation = violationRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));

        Student student = violation.getStudent();
        if (student == null && violation.getLoan() != null && violation.getLoan().getUser() != null) {
            student = violation.getLoan().getUser().getStudent();
        }

        if (student == null) {
            throw new RuntimeException("Student not found for this fine");
        }

        FinePayment payment = FinePayment.builder()
                .violation(violation)
                .student(student)
                .amountPaid(requestDTO.getAmount())
                .paymentMethod(requestDTO.getMethod())
                .paymentDate(LocalDateTime.now())
                .build();

        finePaymentRepository.save(payment);

        // Cập nhật trạng thái Violation
        List<FinePayment> payments = finePaymentRepository.findByViolation_IdOrderByPaymentDateDesc(fineId);
        double totalPaid = payments.stream().mapToDouble(FinePayment::getAmountPaid).sum();
        double fineAmount = violation.getFineAmount() != null ? violation.getFineAmount() : 0.0;

        if (totalPaid >= fineAmount) {
            violation.setStatus("PAID");
            violation.setIsPaid(true);
        } else {
            violation.setStatus("PARTIAL");
        }
        violationRepository.save(violation);

        // Giảm công nợ sinh viên
        student.setTotalDebt(Math.max(0, student.getTotalDebt() - requestDTO.getAmount()));
        studentRepository.save(student);

        return fineMapper.finePaymentToFinePaymentResponseDTO(payment);
    }

    @Override
    public PageResponse<FinePaymentResponseDTO> getFinePayments(int page, int size, String studentCode, String method) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<FinePayment> pageData = finePaymentRepository.findPaymentsWithFilters(studentCode, method, pageable);
        return new PageResponse<>(pageData.map(fineMapper::finePaymentToFinePaymentResponseDTO));
    }

    @Override
    public List<FinePaymentResponseDTO> getPaymentHistoryForFine(Long fineId) {
        List<FinePayment> payments = finePaymentRepository.findByViolation_IdOrderByPaymentDateDesc(fineId);
        return fineMapper.toFinePaymentResponseList(payments);
    }

    @Override
    public void sendReceipt(Long fineId) {
        // Logic mô phỏng gửi biên nhận
        Violation violation = violationRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));
        System.out.println("Mock: Đã gửi biên nhận thanh toán cho khoản phạt " + fineId);
    }

    @Override
    public FineBalanceResponseDTO getStudentFineBalance(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        
        return FineBalanceResponseDTO.builder()
                .studentId(student.getId())
                .studentCode(student.getStudentCode())
                .studentName(student.getUser().getFullName())
                .totalDebt(student.getTotalDebt())
                .build();
    }
}
