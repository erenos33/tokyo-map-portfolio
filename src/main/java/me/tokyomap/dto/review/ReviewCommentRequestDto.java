package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ReviewCommentRequestDto (
        @Schema(description = "댓글 내용", example = "정말 공감돼요!")
        @NotBlank String content
){}
