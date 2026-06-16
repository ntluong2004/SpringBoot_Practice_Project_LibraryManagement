package com.library.library_manager.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoanRequestDTO {
    String studentCode;
    Long copyId;
    LocalDateTime dueDate;
    String note;
}
