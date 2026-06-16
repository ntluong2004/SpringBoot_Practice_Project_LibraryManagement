package com.library.library_manager.dto.book;

import com.library.library_manager.dto.shelf.ShelfResponseDTO;
import com.library.library_manager.entity.Shelf;
import lombok.*;
import lombok.experimental.FieldDefaults;

import com.library.library_manager.enums.BookCopyStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookCopyResponseDTO {
    Long id;
    String barcode;
    BookCopyStatus status; // AVAILABLE, BORROWED, DAMAGED, LOST
    Shelf shelf;
    String bookTitle; // Trả về tiêu đề sách để tiện hiển thị
}