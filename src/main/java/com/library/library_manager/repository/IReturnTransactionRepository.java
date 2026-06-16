package com.library.library_manager.repository;

import com.library.library_manager.entity.ReturnTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IReturnTransactionRepository extends JpaRepository<ReturnTransaction, Long> {

    @Query("SELECT r FROM ReturnTransaction r LEFT JOIN r.loan l LEFT JOIN l.user u LEFT JOIN u.student s " +
           "WHERE (:studentCode IS NULL OR s.studentCode = :studentCode OR u.userName = :studentCode) " +
           "AND (:fromDate IS NULL OR r.returnDate >= :fromDate) " +
           "AND (:toDate IS NULL OR r.returnDate <= :toDate)")
    Page<ReturnTransaction> findReturnsWithFilters(
            @Param("studentCode") String studentCode,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}
