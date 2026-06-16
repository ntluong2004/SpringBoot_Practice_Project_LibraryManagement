package com.library.library_manager.dto.return_book;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConditionCheckRequestDTO {
    @NotBlank(message = "condition is required")
    String condition; // Bình thường, hư hỏng nhẹ, mất trang, vv.
}
