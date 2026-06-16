package com.library.library_manager.controller;

import com.library.library_manager.dto.ApiResponse;
import com.library.library_manager.dto.book.BookRequestDTO;
import com.library.library_manager.dto.book.BookResponseDTO;
import com.library.library_manager.entity.Book;
import com.library.library_manager.service.impl.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ApiResponse<List<BookResponseDTO>> getAll(@RequestParam(required = false) String q) {
        return ApiResponse.<List<BookResponseDTO>>builder()
                .data(bookService.findAll())
                .build();
    }

    @PostMapping
    public ApiResponse<BookResponseDTO> create(@RequestBody BookRequestDTO request) {
        return ApiResponse.<BookResponseDTO>builder().data(bookService.create(request)).build();
    }

    @PutMapping("/{bookId}/shelf")
    public ApiResponse<String> updateShelf(@PathVariable Long bookId, @RequestBody String shelf) {
        bookService.updateShelf(bookId, shelf);
        return ApiResponse.<String>builder().message("Updated shelf location").build();
    }

    @PatchMapping("/{bookId}/stock")
    public ApiResponse<String> patchStock(@PathVariable Long bookId, @RequestParam int adjustment) {
        bookService.updateStock(bookId, adjustment);
        return ApiResponse.<String>builder().message("Stock adjusted").build();
    }

    @DeleteMapping("/{bookId}")
    public ApiResponse<Void> delete(@PathVariable Long bookId) {
        // Logic xóa đầu sách (Cascade sẽ tự xóa bản in chưa sử dụng)
        bookService.delete(bookId);
        return ApiResponse.<Void>builder().message("Book deleted").build();
    }
}
