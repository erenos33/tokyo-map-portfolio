package me.tokyomap.domain.restaurant.repository;

import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.dto.restaurant.RestaurantSearchRequestDto;

import java.util.List;

public interface RestaurantRepositoryCustom {
    List<Restaurant> searchByCondition(RestaurantSearchRequestDto requestDto);
}
