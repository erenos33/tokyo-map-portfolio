package me.tokyomap.mapper;

import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.dto.restaurant.GooglePlaceRegisterRequestDto;
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
                .openingHours(r.getOpeningHours())
                .priceRange(r.getPriceRange())
                .phoneNumber(r.getPhoneNumber())
                .build();
    }

    public static Restaurant fromGoogleDto(GooglePlaceRegisterRequestDto dto, User user) {
        return Restaurant.builder()
                .placeId(dto.getPlaceId())
                .name(dto.getName())
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .rating(dto.getRating())
                .openingHours(dto.getOpeningHours())
                .priceRange(dto.getPriceRange())
                .phoneNumber(dto.getPhoneNumber())
                .registeredBy(user)
                .build();
    }
}
