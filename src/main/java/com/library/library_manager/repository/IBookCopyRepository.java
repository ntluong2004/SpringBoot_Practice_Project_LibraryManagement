package com.library.library_manager.repository;

import com.library.library_manager.entity.Book;
import com.library.library_manager.enums.BookCopyStatus;
import com.library.library_manager.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBookCopyRepository extends JpaRepository<BookCopy, Long> {
    Optional<BookCopy> findByBarcode(String barcode);

    List<BookCopy> findByBookAndStatus(Book book, BookCopyStatus status);

    @Query("SELECT bc FROM BookCopy bc WHERE bc.book.bookId = :bookId")
    List<BookCopy> findByBookBookId(@Param("bookId") Long bookId);

    // Tìm bản in theo ID đầu sách VÀ trạng thái (Ví dụ: AVAILABLE, BORROWED)
    List<BookCopy> findByBookBookIdAndStatus(Long bookId, BookCopyStatus status);
}
