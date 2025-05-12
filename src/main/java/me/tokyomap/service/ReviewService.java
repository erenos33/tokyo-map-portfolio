package me.tokyomap.service;

import me.tokyomap.dto.review.ReviewLikeCountResponseDto;
import me.tokyomap.dto.review.ReviewRequestDto;
import me.tokyomap.dto.review.ReviewResponseDto;
import me.tokyomap.dto.review.ReviewStatisticsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public interface ReviewService {
    ReviewResponseDto createReview(ReviewRequestDto dto, String email);

    ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto dto, String email);

    void deleteReview(Long reviewId, String email);

    Page<ReviewResponseDto> getReviewsByRestaurant(Long restaurantId, Pageable pageable);

    void likeReview(Long reviewId, String email);

    void unlikeReview(Long reviewId, String email);

    ReviewLikeCountResponseDto countLikesByReview(Long reviewId);

    Page<ReviewResponseDto> getReviewsWithSort(Long restaurantId, String sortProperty, Sort.Direction direction, Pageable pageable);

    ReviewStatisticsResponseDto getReviewStatistics(Long restaurantId);


}
