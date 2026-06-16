package com.library.library_manager.dto.return_book;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnConfirmRequestDTO {
    @NotNull(message = "loanId is required")
    Long loanId;
    
    String condition; // Tình trạng sách, ví dụ: "Nguyên vẹn"
    Long staffId;     // ID nhân viên thực hiện (có thể lấy từ context bảo mật sau này)
}
