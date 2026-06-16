package com.library.library_manager.mapper;

import com.library.library_manager.dto.return_book.ReturnTransactionResponseDTO;
import com.library.library_manager.entity.ReturnTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReturnMapper {

    @Mapping(target = "returnId", source = "id")
    @Mapping(target = "loanId", expression = "java(rt.getLoan() != null ? rt.getLoan().getId() : null)")
    @Mapping(target = "bookTitle", expression = "java(rt.getLoan() != null && rt.getLoan().getBookCopy() != null && rt.getLoan().getBookCopy().getBook() != null ? rt.getLoan().getBookCopy().getBook().getTitle() : null)")
    @Mapping(target = "barcode", expression = "java(rt.getLoan() != null && rt.getLoan().getBookCopy() != null ? rt.getLoan().getBookCopy().getBarcode() : null)")
    @Mapping(target = "studentCode", expression = "java(rt.getLoan() != null && rt.getLoan().getUser() != null && rt.getLoan().getUser().getStudent() != null ? rt.getLoan().getUser().getStudent().getStudentCode() : null)")
    @Mapping(target = "studentName", expression = "java(rt.getLoan() != null && rt.getLoan().getUser() != null ? rt.getLoan().getUser().getFullName() : null)")
    @Mapping(target = "borrowDate", expression = "java(rt.getLoan() != null ? rt.getLoan().getBorrowDate() : null)")
    @Mapping(target = "dueDate", expression = "java(rt.getLoan() != null ? rt.getLoan().getDueDate() : null)")
    @Mapping(target = "staffName", expression = "java(rt.getStaff() != null && rt.getStaff().getUser() != null ? rt.getStaff().getUser().getFullName() : null)")
    ReturnTransactionResponseDTO returnToReturnTransactionResponseDTO(ReturnTransaction rt);

    List<ReturnTransactionResponseDTO> toReturnTransactionResponseList(List<ReturnTransaction> transactions);
}
