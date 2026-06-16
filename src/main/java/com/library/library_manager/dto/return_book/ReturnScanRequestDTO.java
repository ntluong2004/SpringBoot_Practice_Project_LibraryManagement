package com.library.library_manager.dto.return_book;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnScanRequestDTO {
    @NotBlank(message = "studentCode is required")
    String studentCode;
    @NotBlank(message = "barcode is required")
    String barcode; // copyId có thể quét bằng barcode
}
