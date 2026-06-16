package com.library.library_manager.mapper;

import com.library.library_manager.dto.LoanResponseDTO;
import com.library.library_manager.dto.ReservationResponseDTO;
import com.library.library_manager.entity.Loan;
import com.library.library_manager.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "loanId",       source = "id")
    @Mapping(target = "borrowerName", expression = "java(loan.getUser().getFullName())")
    @Mapping(target = "studentCode",  expression = "java(loan.getUser().getStudent() != null ? loan.getUser().getStudent().getStudentCode() : null)")
    @Mapping(target = "bookTitle",    expression = "java(loan.getBookCopy().getBook().getTitle())")
    @Mapping(target = "isbn",         expression = "java(loan.getBookCopy().getBook().getIsbn())")
    @Mapping(target = "barcode",      expression = "java(loan.getBookCopy().getBarcode())")
    @Mapping(target = "copyId",       expression = "java(loan.getBookCopy().getId())")
    @Mapping(target = "loanDate",     source = "borrowDate")
    LoanResponseDTO loanToLoanResponseDTO(Loan loan);

    List<LoanResponseDTO> toLoansResponseList(List<Loan> loans);

    @Mapping(target = "id",          source = "id")
    @Mapping(target = "bookTitle",   expression = "java(reservation.getBook().getTitle())")
    @Mapping(target = "isbn",        expression = "java(reservation.getBook().getIsbn())")
    @Mapping(target = "copyBarcode", expression = "java(reservation.getBookCopy() != null ? reservation.getBookCopy().getBarcode() : null)")
    ReservationResponseDTO reservationToReservationResponseDTO(Reservation reservation);

    List<ReservationResponseDTO> toReservationResponseList(List<Reservation> reservations);
}
