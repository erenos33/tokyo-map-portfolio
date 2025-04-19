package me.tokyomap.domain.review.repository;

import me.tokyomap.domain.review.entity.Review;
import me.tokyomap.domain.review.entity.ReviewLike;
import me.tokyomap.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    boolean existsByUserAndReview(User user, Review review);

    Optional<ReviewLike> findByUserAndReview(User user, Review review);

    Long countByReview(Review review);
}
