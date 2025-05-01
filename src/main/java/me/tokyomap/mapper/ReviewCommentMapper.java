package me.tokyomap.mapper;

import me.tokyomap.domain.review.entity.ReviewComment;
import me.tokyomap.dto.review.ReviewCommentResponseDto;

public class ReviewCommentMapper {

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


