package com.library.library_manager.repository;

import com.library.library_manager.entity.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBookReviewRepository extends JpaRepository<BookReview, Long> {

    List<BookReview> findByBookBookIdOrderByCreatedAtDesc(Long bookId);

    boolean existsByBookBookIdAndStudent_User_UserName(Long bookId, String username);
}