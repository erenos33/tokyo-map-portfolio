package me.tokyomap.domain.review.repository;

import me.tokyomap.domain.review.entity.Review;
import me.tokyomap.dto.review.ReviewResponseDto;
import me.tokyomap.dto.review.ReviewStatisticsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {


    Page<Review> findByRestaurantId(Long restaurantId, Pageable pageable);

    Page<ReviewResponseDto> searchReviewsWithSorting(Long restaurantId, String sortProperty, Sort.Direction direction, Pageable pageable);

    ReviewStatisticsResponseDto getStatisticsByRestaurantId(Long restaurantId);

}
