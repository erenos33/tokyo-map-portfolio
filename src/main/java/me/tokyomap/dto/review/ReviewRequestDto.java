package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리뷰 작성 요청 DTO")
public class ReviewRequestDto {

    @Schema(description = "음식점 ID", example = "10")
    @NonNull
    private Long restaurantId;

    @Schema(description = "리뷰 본문", example = "정말 맛있어요!")
    @Size(max = 1000)
    private String content;

    @Schema(description = "평점 (1~5)", example = "5")
    @Min(1)
    @Max(5)
    private int rating;
}
