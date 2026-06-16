package com.library.library_manager.service.impl;

import com.library.library_manager.dto.book.BookRequestDTO;
import com.library.library_manager.dto.book.BookResponseDTO;
import com.library.library_manager.dto.book.BookCopyResponseDTO; // Đảm bảo có DTO này
import com.library.library_manager.entity.Book;
import com.library.library_manager.entity.BookCopy;
import com.library.library_manager.enums.BookCopyStatus;
import com.library.library_manager.exception.AppException;
import com.library.library_manager.exception.ErrorCode;
import com.library.library_manager.repository.IBookCopyRepository;
import com.library.library_manager.repository.IBookRepository;
import com.library.library_manager.service.IBookService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookService implements IBookService {

    IBookRepository bookRepository;
    IBookCopyRepository bookCopyRepository;

    @Override
    public List<BookResponseDTO> findAll() {
        return bookRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BookResponseDTO> findByAttr(String keyword, Pageable pageable) {
        return bookRepository.searchBooks(keyword, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public BookResponseDTO findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        return mapToResponseDTO(book);
    }

    @Override
    @Transactional
    public BookResponseDTO create(BookRequestDTO request) {
        // 1. Lưu Book (như cũ)
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .price(request.getPrice())
                .description(request.getDescription())
                .build();
        Book savedBook = bookRepository.save(book);

        // 2. Tạo bản in tự động
        for (int i = 0; i < request.getQuantity(); i++) {
            BookCopy copy = new BookCopy();
            copy.setBook(savedBook);
            copy.setStatus(BookCopyStatus.AVAILABLE);

            // SỬA TẠI ĐÂY: Gán giá trị cho barcode để không bị lỗi NOT NULL
            // Ví dụ: BC-ID-ThờiGian-SốThứTự
            String generatedBarcode = "BC-" + savedBook.getBookId() + "-" + System.currentTimeMillis() + "-" + i;
            copy.setBarcode(generatedBarcode);

            // Nếu Entity BookCopy có trường entryDate, hãy set luôn
            // copy.setEntryDate(LocalDate.now());

            bookCopyRepository.save(copy);
        }

        return mapToResponseDTO(savedBook);
    }

    @Override
    @Transactional
    public BookResponseDTO update(Long id, BookRequestDTO request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());

        return mapToResponseDTO(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        // Kiểm tra xem có bản in nào đang bị mượn không
        boolean hasActiveLoans = book.getBookCopies().stream()
                .anyMatch(copy -> !copy.getStatus().equals(BookCopyStatus.AVAILABLE));

        if (hasActiveLoans) {
            throw new AppException(ErrorCode.CANNOT_DELETE_BOOK);
        }
        bookRepository.delete(book);
    }

    @Override
    @Transactional
    public void updateShelf(Long id, String shelfName) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        // Logic gán shelf tùy thuộc vào việc bạn xử lý quan hệ ManyToMany thế nào
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void updateStock(Long id, int adjustment) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        if (adjustment > 0) {
            // Thêm bản in mới
            for (int i = 0; i < adjustment; i++) {
                BookCopy copy = new BookCopy();
                copy.setBook(book);
                copy.setStatus(BookCopyStatus.AVAILABLE);

                // SINH BARCODE TỰ ĐỘNG ĐỂ TRÁNH LỖI NULL
                String generatedBarcode = "BC-" + book.getBookId() + "-" + System.currentTimeMillis() + "-" + i;
                copy.setBarcode(generatedBarcode);

                // Nếu DB có cột entry_date NOT NULL, hãy gán ngày hiện tại
                // copy.setEntryDate(LocalDate.now());

                bookCopyRepository.save(copy);
            }
        } else if (adjustment < 0) {
            // Logic xóa bản in (giữ nguyên như cũ)
            List<BookCopy> availableCopies = bookCopyRepository.findByBookAndStatus(book, BookCopyStatus.AVAILABLE);
            if (availableCopies.size() < Math.abs(adjustment)) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }
            for (int i = 0; i < Math.abs(adjustment); i++) {
                bookCopyRepository.delete(availableCopies.get(i));
            }
        }
    }

    // Hàm chuyển đổi sang DTO để tránh lộ Entity và lỗi đệ quy
    private BookResponseDTO mapToResponseDTO(Book book) {
        return BookResponseDTO.builder()
                .id(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .description(book.getDescription())
                // Nếu BookResponseDTO có field copies, map tiếp tại đây
                .build();
    }
}