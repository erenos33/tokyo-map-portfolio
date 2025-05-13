package me.tokyomap.domain.review.repository;

import me.tokyomap.domain.review.entity.ReviewComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    /**
     * 指定されたレビューに対するコメントを作成日時の降順でページング取得
     */
    @Query("SELECT c FROM ReviewComment c WHERE c.review.id = :reviewId ORDER BY c.createdAt DESC")
    Page<ReviewComment> findByReviewId(Long reviewId, Pageable pageable);

}
