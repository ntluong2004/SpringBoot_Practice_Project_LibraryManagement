package com.library.library_manager.controller;

import com.library.library_manager.dto.ApiResponse;
import com.library.library_manager.dto.book.BookCopyResponseDTO;
import com.library.library_manager.service.IBookCopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.library.library_manager.enums.BookCopyStatus;

import java.util.List;

@RestController
@RequestMapping("/api/book-copies")
@RequiredArgsConstructor
public class BookCopyController {

    private final IBookCopyService bookCopyService;

    // Lấy danh sách bản in theo đầu sách
    @GetMapping
    public ApiResponse<List<BookCopyResponseDTO>> getCopies(
            @RequestParam Long bookId,
            @RequestParam(required = false) BookCopyStatus status) {
        return ApiResponse.<List<BookCopyResponseDTO>>builder()
                .data(bookCopyService.getByBookId(bookId, status))
                .build();
    }

    // Xem chi tiết một bản in
    @GetMapping("/{copyId}")
    public ApiResponse<BookCopyResponseDTO> getDetail(@PathVariable Long copyId) {
        return ApiResponse.<BookCopyResponseDTO>builder()
                .data(bookCopyService.getById(copyId))
                .build();
    }

    // Cập nhật trạng thái lưu thông (PATCH)
    @PatchMapping("/{copyId}/circulation-status")
    public ApiResponse<BookCopyResponseDTO> updateStatus(
            @PathVariable Long copyId,
            @RequestParam BookCopyStatus status) {
        return ApiResponse.<BookCopyResponseDTO>builder()
                .data(bookCopyService.updateCirculationStatus(copyId, status))
                .build();
    }

    // Xóa bản in
    @DeleteMapping("/{copyId}")
    public ApiResponse<String> delete(@PathVariable Long copyId) {
        bookCopyService.delete(copyId);
        return ApiResponse.<String>builder()
                .data("Book copy deleted successfully")
                .build();
    }
}
