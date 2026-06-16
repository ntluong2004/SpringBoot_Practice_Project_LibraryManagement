package com.library.library_manager.entity;


import com.library.library_manager.dto.book.BookCopyResponseDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shelf")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shelf_id")
    Long id;

    @Column(name = "shelf_code", nullable = false, unique = true, length = 50)
    String shelfCode; // Ví dụ: SHELF-A1

    @Column(name = "area_zone", length = 50)
    String areaZone; // Khu vực: A, B, C...

    @Column(name = "floor_level")
    Integer floorLevel; // Tầng số mấy

    //    @ManyToMany(mappedBy = "shelfLocation")
//    Set<Book> books = new HashSet<>();
//    @ManyToOne(fetch = FetchType.LAZY)
//    BookCopy bookCopy;
}