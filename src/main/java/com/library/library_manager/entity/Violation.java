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
@Table(name = "violation")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Violation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "violation_id")
        Long id;

        @Column(name = "type")
        String type; // Ví dụ: Late Return, Damaged Book

        @Column(name = "fine_amount")
        Double fineAmount = 0.0;

        @Column(name = "is_paid")
        Boolean isPaid = false;

        @ManyToOne
        @JoinColumn(name = "loan_id")
        Loan loan;

        @ManyToOne
        @JoinColumn(name = "student_id")
        Student student;

        @Column(name = "notes")
        String notes;

        @Column(name = "created_at")
        LocalDateTime createdAt = LocalDateTime.now();

        @Column(name = "due_date")
        LocalDateTime dueDate;

        @Column(name = "status", length = 50)
        String status = "UNPAID"; // UNPAID, PARTIAL, PAID, CANCELLED
}