package com.library.library_manager.dto.fine;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineBalanceResponseDTO {
    Long studentId;
    String studentCode;
    String studentName;
    Double totalDebt;
}
