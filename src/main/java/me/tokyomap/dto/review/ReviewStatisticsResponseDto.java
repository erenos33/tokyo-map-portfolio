package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewStatisticsResponseDto {

    @Schema(description = "음식점 ID", example = "13")
    private Long restaurantId;

    @Schema(description = "평균 별점", example = "4.5")
    private Double averageRating;

    @Schema(description = "총 리뷰 수", example = "128")
    private Long totalReviews;
}