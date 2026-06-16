package com.library.library_manager.dto.fine;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineRequestDTO {
    @NotNull(message = "studentCode is required")
    String studentCode;

    @NotBlank(message = "type is required")
    String type;

    @NotNull(message = "fineAmount is required")
    Double fineAmount;

    String notes;
    LocalDateTime dueDate;
    
    // Tùy chọn nếu liên kết với phiếu mượn
    Long loanId;
}
