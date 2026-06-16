package com.library.library_manager.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowedItemResponse {
    private String bookTitle;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private String status; // Sắp đến hạn, Quá hạn, Đang mượn
}
