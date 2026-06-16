package com.library.library_manager.dto.book;

import com.library.library_manager.dto.category.CategoryRequestDTO;
import com.library.library_manager.dto.shelf.ShelfRequestDTO;
import com.library.library_manager.enums.BookCopyStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookRequestDTO {

    @NotBlank(message = "Title cannot be blank")
    String title;

    @NotBlank(message = "Author cannot be blank")
    String author;

    @NotNull(message = "Publisher is not null")
    String publisher;

    @NotBlank(message = "ISBN cannot be blank")
    String isbn;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    Double price;

    String description;

    @NotBlank(message = "Book status cannot be blank")
    String status;

    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity;

    Set<CategoryRequestDTO> category;

    Set<ShelfRequestDTO> shelfLocation;

    List<BookRequestDTO> bookCopy;


}