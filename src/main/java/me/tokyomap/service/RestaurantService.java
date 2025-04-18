package me.tokyomap.service;

import me.tokyomap.dto.restaurant.RestaurantSearchRequestDto;
import me.tokyomap.dto.restaurant.RestaurantSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RestaurantService {

    Page<RestaurantSearchResponseDto> searchRestaurants(RestaurantSearchRequestDto requestDto, Pageable pageable);

    RestaurantSearchResponseDto getRestaurantById(Long id);
}
