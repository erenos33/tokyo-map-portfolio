package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 特定のレビューに対する「いいね」数を返すレスポンスDTO
 */
@Getter
@AllArgsConstructor
public class ReviewLikeCountResponseDto {

    @Schema(description = "レビューID", example = "6")
    private Long reviewId;

    @Schema(description = "いいね数", example = "3")
    private Long likeCount;
}
