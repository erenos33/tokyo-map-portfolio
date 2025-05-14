package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * レビュー投稿用のリクエストDTO
 * レストランID、レビュー内容、評価スコアを含む
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "レビュー投稿リクエストDTO")
public class ReviewRequestDto {

    @Schema(description = "レストランID", example = "10")
    @NonNull
    private Long restaurantId;

    @Schema(description = "レビュー本文", example = "本当に美味しかったです！")
    @Size(max = 1000)
    private String content;

    @Schema(description = "評価スコア（1〜5）", example = "5")
    @Min(1)
    @Max(5)
    private int rating;
}
