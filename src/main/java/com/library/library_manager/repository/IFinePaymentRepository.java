package com.library.library_manager.repository;

import com.library.library_manager.entity.FinePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFinePaymentRepository extends JpaRepository<FinePayment, Long> {
    // Truy vấn: FinePayment -> Student -> User -> UserName
    List<FinePayment> findByStudent_User_UserNameOrderByPaymentDateDesc(String username);

    @Query("SELECT fp FROM FinePayment fp " +
           "WHERE (:studentCode IS NULL OR fp.student.studentCode = :studentCode) " +
           "AND (:method IS NULL OR fp.paymentMethod = :method)")
    Page<FinePayment> findPaymentsWithFilters(
            @Param("studentCode") String studentCode,
            @Param("method") String method,
            Pageable pageable);

    List<FinePayment> findByViolation_IdOrderByPaymentDateDesc(Long violationId);
}
