package com.library.library_manager.repository;

import com.library.library_manager.entity.Violation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IViolationRepository extends JpaRepository<Violation, Long> {
    // Lấy danh sách vi phạm dựa trên username thông qua bảng Loan
    List<Violation> findByLoan_User_UserName(String username);

    @Query("SELECT v FROM Violation v LEFT JOIN v.student s LEFT JOIN v.loan l LEFT JOIN l.user u " +
           "WHERE (:studentCode IS NULL OR s.studentCode = :studentCode OR u.userName = :studentCode) " +
           "AND (:status IS NULL OR v.status = :status) " +
           "AND (:type IS NULL OR v.type = :type)")
    Page<Violation> findFinesWithFilters(
            @Param("studentCode") String studentCode,
            @Param("status") String status,
            @Param("type") String type,
            Pageable pageable);
}
