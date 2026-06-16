package com.library.library_manager.dto.return_book;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IssueRequestDTO {
    @NotBlank(message = "type is required")
    String type; // LOAN_DAMAGED, LOAN_LOST

    @NotNull(message = "fineAmount is required")
    Double fineAmount;

    String notes;
}
