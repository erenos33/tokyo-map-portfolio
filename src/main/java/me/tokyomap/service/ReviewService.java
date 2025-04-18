package me.tokyomap.service;

import me.tokyomap.dto.review.ReviewRequestDto;
import me.tokyomap.dto.review.ReviewResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    void createReview(ReviewRequestDto dto);

    Page<ReviewResponseDto> getReviewsByRestaurant(Long restaurantId, Pageable pageable);
}
