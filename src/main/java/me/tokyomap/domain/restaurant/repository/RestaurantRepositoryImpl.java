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

@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Restaurant> searchByCondition(RestaurantSearchRequestDto requestDto, Pageable pageable) {
        QRestaurant restaurant = QRestaurant.restaurant;
        BooleanBuilder builder = new BooleanBuilder();

        // ⬇⬇⬇ 여기에서 먼저 값 다듬기
        String category = Optional.ofNullable(requestDto.getCategory())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse(null);

        String city = Optional.ofNullable(requestDto.getCity())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse(null);

        Boolean openNow = requestDto.getOpenNow();

        //조건 1: category
        if(category != null) {
            builder.and(restaurant.category.like("%" + category + "%"));
        }

        //조건 2: city(address에 포함되는지)
        if (city != null) {
            builder.and(restaurant.address.like("%" + city + "%"));
        }

        //조건3 : openNow (openingHours 파싱해서 간단히 비교)
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

        long total = queryFactory
                .selectFrom(restaurant)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}
