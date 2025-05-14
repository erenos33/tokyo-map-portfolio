package me.tokyomap.mapper;

import me.tokyomap.domain.review.entity.ReviewComment;
import me.tokyomap.dto.review.ReviewCommentResponseDto;

/**
 * ReviewCommentエンティティをReviewCommentResponseDtoに変換するマッパークラス
 */
public class ReviewCommentMapper {

    /**
     * コメントエンティティをDTOに変換し、現在のユーザーが作成者かどうかも判定する
     */
    public static ReviewCommentResponseDto toDto(ReviewComment comment, String currentEmail) {
        return new ReviewCommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getNickname(),
                comment.getCreatedAt(),
                comment.getUser().getEmail().equals(currentEmail)
        );
    }
}


