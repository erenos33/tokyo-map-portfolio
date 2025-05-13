package me.tokyomap.domain.review.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.tokyomap.domain.review.entity.QReview;
import me.tokyomap.domain.review.entity.QReviewLike;
import me.tokyomap.dto.review.ReviewResponseDto;
import me.tokyomap.dto.review.ReviewStatisticsResponseDto;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * QueryDSLを使用してレビューの動的検索および統計を実装するリポジトリ
 * ソート条件に応じてlikeCount・createdAtなどで並べ替え可能
 */
@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewResponseDto> searchReviewsWithSorting(Long restaurantId, String sortProperty, Sort.Direction direction, Pageable pageable) {

        QReview review = QReview.review;
        QReviewLike reviewLike = QReviewLike.reviewLike;

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortProperty, direction, review, reviewLike);

        List<Tuple> results = queryFactory
                .select(review, reviewLike.count())
                .from(review)
                .leftJoin(reviewLike).on(review.eq(reviewLike.review))
                .where(review.restaurant.id.eq(restaurantId))
                .groupBy(review.id)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ReviewResponseDto> content = results.stream()
                .map(tuple -> {
                    var r = tuple.get(review);
                    Long likeCount = tuple.get(reviewLike.count());
                    return new ReviewResponseDto(
                            r.getId(),
                            r.getUser().getId(),
                            r.getUser().getNickname(),
                            r.getContent(),
                            r.getRating(),
                            r.getCreatedAt(),
                            likeCount
                    );
                })
                .collect(Collectors.toList());

        Long total = queryFactory
                .select(review.count())
                .from(review)
                .where(review.restaurant.id.eq(restaurantId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public ReviewStatisticsResponseDto getStatisticsByRestaurantId(Long restaurantId) {
        QReview review = QReview.review;

        Double average = queryFactory
                .select(review.rating.avg())
                .from(review)
                .where(review.restaurant.id.eq(restaurantId))
                .fetchOne();

        Long count = queryFactory
                .select(review.count())
                .from(review)
                .where(review.restaurant.id.eq(restaurantId))
                .fetchOne();

        return new ReviewStatisticsResponseDto(
                restaurantId,
                average != null ? Math.round(average * 10.0) / 10.0 : 0.0,
                count != null ? count : 0L
        );
    }


    private OrderSpecifier<?> getOrderSpecifier(String property, Sort.Direction direction, QReview review, QReviewLike reviewLike) {
        boolean isAsc = direction.isAscending();

        switch (property) {
            case "likeCount":
                return isAsc
                        ? reviewLike.count().asc()
                        : reviewLike.count().desc();
            case "createdAt":
                return isAsc
                        ? review.createdAt.asc()
                        : review.createdAt.desc();
            case "updatedAt":
                return isAsc
                        ? review.updatedAt.asc()
                        : review.updatedAt.desc();
            case "rating":
                return isAsc
                        ? review.rating.asc()
                        : review.rating.desc();
            default:
                return review.createdAt.desc(); // 기본값
        }
    }
}
