package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리뷰 응답 DTO")
public class ReviewResponseDto {

    @Schema(description = "리뷰 ID", example = "1")
    private Long id;

    @Schema(description = "작성자 닉네임", example = "도쿄맛집러버")
    private String nickname;

    @Schema(description = "리뷰 본문", example = "돈카츠가 바삭하고 너무 맛있었어요!")
    private String content;

    @Schema(description = "평점", example = "5")
    private int rating;

    @Schema(description = "작성일자", example = "2025-04-18T13:45:30")
    private LocalDateTime createdAt;
}
