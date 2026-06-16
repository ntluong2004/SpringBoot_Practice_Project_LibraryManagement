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
@Table(name = "loan")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Loan {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "loan_id")
        Long id;

        @Column(name = "borrow_date", nullable = false)
        LocalDateTime borrowDate = LocalDateTime.now();

        @Column(name = "due_date", nullable = false)
        LocalDateTime dueDate;

        @Column(name = "returned_at")
        LocalDateTime returnedAt;

        @Column(name = "status")
        String status; // Ví dụ: Active, Returned, Overdue

        @Column(name = "return_status")
        private String returnStatus; // Tình trạng sách khi trả (Nguyên vẹn, Hư hỏng)

        @Column(name = "staff_note")
        private String staffNote; // Ghi chú của thủ thư
        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        User user;

        @ManyToOne
        @JoinColumn(name = "copy_id", nullable = false)
        BookCopy bookCopy;

        @ManyToOne
        @JoinColumn(name = "staff_id")
        Staff staff;

        @OneToOne(mappedBy = "loan")
        private Reservation reservation;
}
