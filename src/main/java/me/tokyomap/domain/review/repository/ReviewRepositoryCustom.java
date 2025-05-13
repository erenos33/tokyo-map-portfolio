package me.tokyomap.domain.review.repository;

import me.tokyomap.dto.review.ReviewResponseDto;
import me.tokyomap.dto.review.ReviewStatisticsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * レビューの条件付き検索および統計情報取得のカスタムインターフェース
 */
public interface ReviewRepositoryCustom {
    Page<ReviewResponseDto> searchReviewsWithSorting(Long restaurantId, String sortProperty, Sort.Direction direction, Pageable pageable);

    ReviewStatisticsResponseDto getStatisticsByRestaurantId(Long restaurantId);

}
