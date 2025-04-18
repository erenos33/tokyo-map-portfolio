package me.tokyomap.mapper;

import me.tokyomap.domain.review.entity.Review;
import me.tokyomap.dto.review.ReviewResponseDto;

public class ReviewMapper {

    public static ReviewResponseDto toDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .nickname(review.getUser().getNickname())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
