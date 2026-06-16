package com.library.library_manager.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationResponseDTO {
    Long id;
    String bookTitle;
    String isbn;
    String copyBarcode; // Bản in được giữ (nếu đã assign)
    LocalDateTime requestDate;
    LocalDateTime expirationDate;
    String status;
}
