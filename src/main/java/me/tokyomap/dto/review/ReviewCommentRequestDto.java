package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * レビューに対するコメント投稿のためのリクエストDTO
 */
public record ReviewCommentRequestDto (
        @Schema(description = "コメント内容", example = "とても共感できます！")
        @NotBlank String content
){}
