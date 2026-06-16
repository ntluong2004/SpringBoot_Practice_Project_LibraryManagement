package com.library.library_manager.dto.fine;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinePaymentRequestDTO {
    @NotNull(message = "amount is required")
    Double amount;

    String method; // Tiền mặt, Chuyển khoản, etc.
}
