package com.library.library_manager.repository;

import com.library.library_manager.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ILoanRepository extends JpaRepository<Loan, Long> {
    // Lấy danh sách sách đang mượn (chưa trả) của một sinh viên theo username
    Optional<Loan> findByUser_Student_StudentCodeAndBookCopy_BarcodeAndReturnedAtIsNull(
            String studentCode,
            String barcode
    );

    List<Loan> findByUser_UserNameAndReturnedAtIsNull(String username);

    // Đếm để check hạn mức
    long countByUser_UserNameAndReturnedAtIsNull(String username);

    // Lấy tất cả loan theo studentCode (qua quan hệ User -> Student)
    List<Loan> findByUser_Student_StudentCode(String studentCode);

    @Query("SELECT l FROM Loan l WHERE l.user.userId IN (SELECT s.user.userId FROM Student s WHERE s.studentCode = :studentCode)")
    List<Loan> findByStudentCode(@Param("studentCode") String studentCode);

    // Lấy loan theo studentId (user.userId của student, do @MapsId student.id == user.userId)
    List<Loan> findByUser_UserIdAndReturnedAtIsNull(Long userId);

    // Lấy tất cả loan theo userId (để hiển thị toàn bộ lịch sử)
    List<Loan> findByUser_UserId(Long userId);

    // Lấy loan theo status
    List<Loan> findByStatus(String status);

    // Lấy loan theo status và studentCode
    @Query("SELECT l FROM Loan l WHERE l.status = :status AND l.user.userId IN (SELECT s.user.userId FROM Student s WHERE s.studentCode = :studentCode)")
    List<Loan> findByStatusAndStudentCode(@Param("status") String status, @Param("studentCode") String studentCode);
}

