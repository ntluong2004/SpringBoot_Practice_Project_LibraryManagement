package com.library.library_manager.dto.return_book;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnTransactionResponseDTO {
    Long returnId;
    Long loanId;
    
    String bookTitle;
    String barcode;
    String studentCode;
    String studentName;
    
    LocalDateTime borrowDate;
    LocalDateTime dueDate;
    LocalDateTime returnDate;
    
    String actualCondition;
    Double lateFee;
    String staffName;
}
