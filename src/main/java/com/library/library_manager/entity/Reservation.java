package com.library.library_manager.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reservation {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "reservation_id")
        Long id;

        @Column(name = "request_date", nullable = false)
        LocalDateTime requestDate = LocalDateTime.now();

        @Column(name = "expiration_date")
        LocalDateTime expirationDate;

        @Column(name = "status", length = 50)
        String status; // PENDING, FULFILLED, CANCELLED, EXPIRED

        // 1. Nối với Student (Ai đặt?)
        @ManyToOne
        @JoinColumn(name = "student_id", nullable = false)
        Student student;

        // 2. Nối với Book (Đầu sách nào?)
        @ManyToOne
        @JoinColumn(name = "book_id", nullable = false)
        Book book;

        // 3. Nối với BookCopies (Đặt đúng cuốn nào - Nếu hệ thống cho phép chọn bản sao cụ thể)
        @ManyToOne
        @JoinColumn(name = "copy_id")
        BookCopy bookCopy;

        // 4. Nối với Loan (Giao dịch mượn sinh ra từ việc đặt trước này)
        @OneToOne
        @JoinColumn(name = "loan_id")
        Loan loan;
}
