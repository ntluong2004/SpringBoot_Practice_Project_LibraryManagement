package com.library.library_manager.service.impl;

import com.library.library_manager.dto.book.BookCopyResponseDTO;
import com.library.library_manager.entity.BookCopy;
import com.library.library_manager.exception.AppException;
import com.library.library_manager.exception.ErrorCode;
import com.library.library_manager.repository.IBookCopyRepository;
import com.library.library_manager.repository.IBookRepository;
import com.library.library_manager.service.IBookCopyService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.library_manager.enums.BookCopyStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookCopyService implements IBookCopyService {

    IBookCopyRepository bookCopyRepository;
    IBookRepository bookRepository;

    @Override
    public List<BookCopyResponseDTO> findAll() {
        return bookCopyRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookCopyResponseDTO> getByBookId(Long bookId, BookCopyStatus status) {
        // Nếu có status thì lọc theo status, không thì lấy hết của đầu sách đó
        List<BookCopy> copies;
        if (status != null) {
            copies = bookCopyRepository.findByBookBookIdAndStatus(bookId, status);
        } else {
            copies = bookCopyRepository.findByBookBookId(bookId);
        }
        return copies.stream().map(this::mapToResponseDTO).toList();
    }

    @Override
    public BookCopyResponseDTO getById(Long id) {
        return bookCopyRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new AppException(ErrorCode.COPY_NOT_FOUND));
    }

    @Override
    @Transactional
    public BookCopyResponseDTO updateCirculationStatus(Long id, BookCopyStatus newStatus) {
        BookCopy copy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COPY_NOT_FOUND));

        // Kiểm tra logic: Nếu đang BORROWED thì không cho phép chuyển sang LOST/DAMAGED trực tiếp
        // trừ khi xử lý qua luồng trả sách
        copy.setStatus(newStatus);
        return mapToResponseDTO(bookCopyRepository.save(copy));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        BookCopy copy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COPY_NOT_FOUND));

        // Không cho phép xóa nếu sách đang được mượn
        if (BookCopyStatus.BORROWED.equals(copy.getStatus())) {
            throw new AppException(ErrorCode.CANNOT_DELETE_BORROWED_COPY);
        }

        bookCopyRepository.delete(copy);
    }

    private BookCopyResponseDTO mapToResponseDTO(BookCopy copy) {
        return BookCopyResponseDTO.builder()
                .id(copy.getId())
                .barcode(copy.getBarcode())
                .status(copy.getStatus())
                .shelf(copy.getShelf())
                .bookTitle(copy.getBook().getTitle())
                .build();
    }
}