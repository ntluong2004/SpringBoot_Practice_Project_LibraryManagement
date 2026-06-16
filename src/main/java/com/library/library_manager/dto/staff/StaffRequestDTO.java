package com.library.library_manager.dto.staff;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffRequestDTO {
    String fullName;      // Lấy trực tiếp, không cần lồng qua User
    Long positionId;
}
