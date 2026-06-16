package com.library.library_manager.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.library.library_manager.dto.book.BookCopyResponseDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

import com.library.library_manager.enums.BookCopyStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_copy")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "copy_id")
    Long id;

    @Column(name = "barcode", unique = true, nullable = false)
    String barcode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    BookCopyStatus status; // Ví dụ: AVAILABLE, BORROWED, LOST

    @Column(name = "is_locked")
    Boolean isLocked = false;

    @Column(name = "entry_date")
    LocalDateTime entryDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "shelf_id")
    Shelf shelf;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    Book book;
}
