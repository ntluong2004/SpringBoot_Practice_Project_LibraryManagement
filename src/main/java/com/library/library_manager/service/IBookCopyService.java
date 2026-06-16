package com.library.library_manager.service;

import com.library.library_manager.dto.book.BookCopyResponseDTO;
import com.library.library_manager.entity.BookCopy;
import com.library.library_manager.enums.BookCopyStatus;
import java.util.List;
import java.util.Optional;

public interface IBookCopyService {
    // Khớp với GET /api/book-copies?bookId=xxx
    List<BookCopyResponseDTO> getByBookId(Long bookId, BookCopyStatus status);

    // Khớp với GET /api/book-copies/{copyId}
    BookCopyResponseDTO getById(Long id);

    // Khớp với PATCH /api/book-copies/{copyId}/circulation-status
    BookCopyResponseDTO updateCirculationStatus(Long id, BookCopyStatus status);

    // Khớp với DELETE /api/book-copies/{copyId}
    void delete(Long id);

    // Giữ lại nếu bạn vẫn cần lấy tất cả
    List<BookCopyResponseDTO> findAll();
}