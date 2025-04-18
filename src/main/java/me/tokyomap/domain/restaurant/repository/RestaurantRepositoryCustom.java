package me.tokyomap.domain.restaurant.repository;

import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.dto.restaurant.RestaurantSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface RestaurantRepositoryCustom {
    Page<Restaurant> searchByCondition(RestaurantSearchRequestDto requestDto, Pageable pageable);
}
