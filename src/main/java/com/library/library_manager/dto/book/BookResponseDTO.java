package com.library.library_manager.dto.book;

import com.library.library_manager.dto.shelf.ShelfResponseDTO;
import com.library.library_manager.entity.Shelf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookResponseDTO {
    Long id;
    String title;
    String author;
    String isbn;
    String genre;
    Double price;
    String description;
    Integer publishYear;
    Set<Shelf> shelfLocation;

    // Trả về danh sách bản in tối giản
    List<BookCopyResponseDTO> copies;
}