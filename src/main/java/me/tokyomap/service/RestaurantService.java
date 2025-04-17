package me.tokyomap.service;

import me.tokyomap.dto.restaurant.RestaurantSearchRequestDto;
import me.tokyomap.dto.restaurant.RestaurantSearchResponseDto;

import java.util.List;

public interface RestaurantService {

    List<RestaurantSearchResponseDto> searchRestaurants(RestaurantSearchRequestDto requestDto);

    RestaurantSearchResponseDto getRestaurantById(Long id);
}
