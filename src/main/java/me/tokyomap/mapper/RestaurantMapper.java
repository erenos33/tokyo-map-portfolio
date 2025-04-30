package me.tokyomap.mapper;

import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.dto.restaurant.RestaurantSearchResponseDto;

public class RestaurantMapper {
    public static RestaurantSearchResponseDto toDto(Restaurant r) {
        return RestaurantSearchResponseDto.builder()
                .id(r.getId())
                .name(r.getName())
                .address(r.getAddress())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .rating(r.getRating())
                .build();
    }
}
