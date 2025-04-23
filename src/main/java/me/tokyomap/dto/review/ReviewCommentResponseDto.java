package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReviewCommentResponseDto(
        @Schema(description = "댓글 ID", example = "1")
        Long id,

        @Schema(description = "댓글 내용", example = "정말 공감돼요!")
        String content,

        @Schema(description = "작성자 닉네임", example = "도쿄맛집러버")
        String nickname,

        @Schema(description = "작성일자", example = "2025-04-23T12:40:00")
        LocalDateTime createdAt


) {
}
