package me.tokyomap.mapper;

import me.tokyomap.domain.review.entity.Review;
import me.tokyomap.dto.review.ReviewResponseDto;

/**
 * ReviewエンティティをReviewResponseDtoに変換するマッパークラス
 */
public class ReviewMapper {

    /**
     * レビューエンティティからレスポンスDTOを生成する
     */
    public static ReviewResponseDto toDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .authorId(review.getUser().getId())
                .nickname(review.getUser().getNickname())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
