package com.library.library_manager.repository;

import com.library.library_manager.entity.Student;
import com.library.library_manager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IStudentRepository extends JpaRepository<Student, Long> {

    // 1. Tìm sinh viên theo mã số sinh viên (student_code)
    Optional<Student> findByStudentCode(String studentCode);

    // 2. Kiểm tra sự tồn tại của mã sinh viên (phục vụ lúc tạo mới)
    boolean existsByStudentCode(String studentCode);

    // 3. Tìm kiếm và phân trang với bộ lọc (mã số và trạng thái) theo ảnh 02dc3b.png
    @Query("SELECT s FROM Student s WHERE " +
            "(:studentCode IS NULL OR s.studentCode LIKE %:studentCode%) AND " +
            "(:status IS NULL OR s.status = :status)")
    Page<Student> findAllWithFilters(
            @Param("studentCode") String studentCode,
            @Param("status") String status,
            Pageable pageable);

    // 4. Tìm kiếm theo Username của bảng User (thông qua mối quan hệ @OneToOne)
    Optional<Student> findByUser_UserName(String userName);

    Optional<Student> findByUser(User user);

}