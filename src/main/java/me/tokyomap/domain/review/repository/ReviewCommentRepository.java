package me.tokyomap.domain.review.repository;

import me.tokyomap.domain.review.entity.ReviewComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    @Query("SELECT c FROM ReviewComment c WHERE c.review.id = :reviewId ORDER BY c.createdAt DESC")
    Page<ReviewComment> findByReviewId(Long reviewId, Pageable pageable);
    List<ReviewComment> findByReviewId(Long reviewId);
}
