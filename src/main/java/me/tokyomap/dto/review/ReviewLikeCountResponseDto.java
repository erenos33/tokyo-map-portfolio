package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewLikeCountResponseDto {

    @Schema(description = "리뷰 ID", example = "6")
    private Long reviewId;

    @Schema(description = "좋아요 수", example = "3")
    private Long likeCount;
}
