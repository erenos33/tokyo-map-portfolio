package me.tokyomap.dto.review;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * レビュー情報を返すレスポンスDTO
 * 投稿者情報、レビュー内容、評価、投稿日時、いいね数を含む
 */
@Data
@NoArgsConstructor
@Builder
@Schema(description = "レビュー応答DTO")
public class ReviewResponseDto {

    @Schema(description = "レビューID", example = "1")
    private Long id;

    @Schema(description = "投稿者ID", example = "42")
    private Long authorId;

    @Schema(description = "投稿者のニックネーム", example = "東京グルメラバー")
    private String nickname;

    @Schema(description = "レビュー本文", example = "とんかつがサクサクでとても美味しかったです！")
    private String content;

    @Schema(description = "評価スコア", example = "5")
    private int rating;

    @Schema(description = "投稿日時", example = "2025-04-18T13:45:30")
    private LocalDateTime createdAt;

    @Schema(description = "いいね数", example = "3")
    private Long likeCount;

    /**
     * クエリで直接DTOを生成するためのコンストラクタ
     * 投稿者情報、レビュー内容、評価、作成日時、いいね数を初期化する
     */
    @QueryProjection
    public ReviewResponseDto(Long id, Long authorId, String nickname, String content, int rating, LocalDateTime createdAt, Long likeCount) {
        this.id = id;
        this.authorId = authorId;
        this.nickname = nickname;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
    }
}
