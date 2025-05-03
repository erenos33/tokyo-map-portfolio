package me.tokyomap.service;

import me.tokyomap.dto.restaurant.GooglePlaceRegisterRequestDto;
import me.tokyomap.dto.restaurant.RestaurantSearchRequestDto;
import me.tokyomap.dto.restaurant.RestaurantSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RestaurantService {

    Page<RestaurantSearchResponseDto> searchRestaurants(RestaurantSearchRequestDto requestDto, Pageable pageable);

    RestaurantSearchResponseDto getRestaurantById(Long id);

    Long registerGooglePlace(GooglePlaceRegisterRequestDto dto, String userEmail);

    Page<RestaurantSearchResponseDto> getMyRegisteredRestaurants(String userEmail, Pageable pageable);

    void deleteMyRestaurant(Long restaurantId, String userEmail);

    void deleteRestaurantByAdmin(Long restaurantId);


}
