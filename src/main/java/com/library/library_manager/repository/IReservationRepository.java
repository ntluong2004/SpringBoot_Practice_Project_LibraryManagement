package com.library.library_manager.repository;

import com.library.library_manager.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {
    // Tìm qua: Reservation -> Student -> User -> UserName
    List<Reservation> findByStudent_User_UserNameOrderByRequestDateDesc(String username);

    // Đếm số lượng đặt trước đang PENDING để kiểm tra hạn mức
    long countByStudent_User_UserNameAndStatus(String username, String status);

    // Tìm danh sách đặt trước theo Username và Trạng thái
    List<Reservation> findByStudent_User_UserNameAndStatus(String username, String status);

    // Tìm reservation của một student theo studentId (user_id)
    List<Reservation> findByStudent_IdAndStatus(Long studentId, String status);

    // Đếm reservation PENDING của student
    long countByStudent_IdAndStatus(Long studentId, String status);
}