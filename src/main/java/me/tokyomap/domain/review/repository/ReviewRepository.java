package me.tokyomap.domain.review.repository;

import me.tokyomap.domain.review.entity.Review;
import me.tokyomap.dto.review.ReviewResponseDto;
import me.tokyomap.dto.review.ReviewStatisticsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    //특정 음식점에 달린 리뷰 목록 조회
    Page<Review> findByRestaurantId(Long restaurantId, Pageable pageable);

    List<Review> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);

    Page<ReviewResponseDto> searchReviewsWithSorting(Long restaurantId, String sortProperty, Sort.Direction direction, Pageable pageable);

    ReviewStatisticsResponseDto getStatisticsByRestaurantId(Long restaurantId);

    // TODO: 리뷰 리스트에 user, restaurant 연관 데이터 fetch join 적용 여부 검토
}
