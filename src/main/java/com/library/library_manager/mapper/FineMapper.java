package com.library.library_manager.mapper;

import com.library.library_manager.dto.fine.FinePaymentResponseDTO;
import com.library.library_manager.dto.fine.FineResponseDTO;
import com.library.library_manager.entity.FinePayment;
import com.library.library_manager.entity.Violation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FineMapper {

    @Mapping(target = "fineAmount", expression = "java(violation.getFineAmount() != null ? violation.getFineAmount() : 0.0)")
    @Mapping(target = "studentId", expression = "java(violation.getStudent() != null ? violation.getStudent().getId() : (violation.getLoan() != null && violation.getLoan().getUser() != null && violation.getLoan().getUser().getStudent() != null ? violation.getLoan().getUser().getStudent().getId() : null))")
    @Mapping(target = "studentCode", expression = "java(violation.getStudent() != null ? violation.getStudent().getStudentCode() : (violation.getLoan() != null && violation.getLoan().getUser() != null && violation.getLoan().getUser().getStudent() != null ? violation.getLoan().getUser().getStudent().getStudentCode() : null))")
    @Mapping(target = "studentName", expression = "java(violation.getStudent() != null && violation.getStudent().getUser() != null ? violation.getStudent().getUser().getFullName() : (violation.getLoan() != null && violation.getLoan().getUser() != null ? violation.getLoan().getUser().getFullName() : null))")
    @Mapping(target = "loanId", expression = "java(violation.getLoan() != null ? violation.getLoan().getId() : null)")
    @Mapping(target = "bookTitle", expression = "java(violation.getLoan() != null && violation.getLoan().getBookCopy() != null && violation.getLoan().getBookCopy().getBook() != null ? violation.getLoan().getBookCopy().getBook().getTitle() : null)")
    FineResponseDTO violationToFineResponseDTO(Violation violation);

    List<FineResponseDTO> toFineResponseList(List<Violation> violations);

    @Mapping(target = "fineId", expression = "java(payment.getViolation() != null ? payment.getViolation().getId() : null)")
    @Mapping(target = "fineType", expression = "java(payment.getViolation() != null ? payment.getViolation().getType() : null)")
    @Mapping(target = "studentCode", expression = "java(payment.getStudent() != null ? payment.getStudent().getStudentCode() : null)")
    @Mapping(target = "studentName", expression = "java(payment.getStudent() != null && payment.getStudent().getUser() != null ? payment.getStudent().getUser().getFullName() : null)")
    FinePaymentResponseDTO finePaymentToFinePaymentResponseDTO(FinePayment payment);

    List<FinePaymentResponseDTO> toFinePaymentResponseList(List<FinePayment> payments);
}
