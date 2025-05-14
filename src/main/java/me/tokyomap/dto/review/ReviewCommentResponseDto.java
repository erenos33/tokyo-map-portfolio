package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * レビューコメントのレスポンスDTO
 * コメントの内容、作成者、作成日時、自分のコメントかどうかなどを含む
 */
public record ReviewCommentResponseDto(
        @Schema(description = "コメントID", example = "1")
        Long id,

        @Schema(description = "コメント内容", example = "とても共感できます！")
        String content,

        @Schema(description = "投稿者のニックネーム", example = "東京グルメラバー")
        String nickname,

        @Schema(description = "作成日時", example = "2025-04-23T12:40:00")
        LocalDateTime createdAt,

        @Schema(description = "現在のログインユーザーが作成者かどうかを示すフラグ", example = "true")
        boolean isAuthor
) {
}
