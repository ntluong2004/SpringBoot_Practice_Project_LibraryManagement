package com.library.library_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "book_id")
        private Long bookId; // Đặt tên là bookId cho đúng chuẩn CamelCase

        private String title;
        private String author;
        private String publisher;
        private String isbn;
        private Double price;
        private String description;
        private String status;

        // Quan hệ n-n với Category
        @ManyToMany
        @JoinTable(
                name = "book_category",
                joinColumns = @JoinColumn(name = "book_id"),
                inverseJoinColumns = @JoinColumn(name = "category_id")
        )
        @Builder.Default
        Set<Category> categories = new HashSet<>();

        // Quan hệ n-n với Shelf
        @ManyToMany
        @JoinTable(
                name = "book_shelf",
                joinColumns = @JoinColumn(name = "book_id"),
                inverseJoinColumns = @JoinColumn(name = "shelf_id")
        )
        @Builder.Default
        Set<Shelf> shelfLocation = new HashSet<>();

        @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        @Builder.Default
        List<BookCopy> bookCopies = new ArrayList<>();
}