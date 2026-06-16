package com.library.library_manager.dto.fine;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinePaymentResponseDTO {
    Long id;
    Double amountPaid;
    LocalDateTime paymentDate;
    String paymentMethod;
    
    Long fineId;
    String fineType;
    
    String studentCode;
    String studentName;
}
