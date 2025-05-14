package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 特定レストランに関するレビュー統計情報を返すレスポンスDTO
 * 平均評価とレビュー件数を含む
 */
@Getter
@AllArgsConstructor
public class ReviewStatisticsResponseDto {

    @Schema(description = "レストランID", example = "13")
    private Long restaurantId;

    @Schema(description = "平均評価", example = "4.5")
    private Double averageRating;

    @Schema(description = "総レビュー数", example = "128")
    private Long totalReviews;
}