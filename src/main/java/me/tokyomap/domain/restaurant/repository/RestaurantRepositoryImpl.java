package me.tokyomap.domain.restaurant.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.tokyomap.domain.restaurant.entity.QRestaurant;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.dto.restaurant.RestaurantSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * QueryDSLを使用してレストランを条件検索するカスタムリポジトリ実装
 * カテゴリ・都市名・現在営業中（営業時間）などの動的条件に対応
 */
@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Restaurant> searchByCondition(RestaurantSearchRequestDto requestDto, Pageable pageable) {
        QRestaurant restaurant = QRestaurant.restaurant;
        BooleanBuilder builder = new BooleanBuilder();

        String category = Optional.ofNullable(requestDto.getCategory())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse(null);

        String city = Optional.ofNullable(requestDto.getCity())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse(null);

        Boolean openNow = requestDto.getOpenNow();

        if(category != null) {
            builder.and(restaurant.category.like("%" + category + "%"));
        }

        if (city != null) {
            builder.and(restaurant.address.like("%" + city + "%"));
        }

        if (Boolean.TRUE.equals(openNow)) {
            LocalTime now = LocalTime.now();
            String hourStr = String.format("%02d:", now.getHour());
            builder.and(restaurant.openingHours.like("%" + hourStr + "%"));
        }

        List<Restaurant> content = queryFactory
                .selectFrom(restaurant)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(restaurant.count())
                .from(restaurant)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);

    }
}
