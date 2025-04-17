package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.restaurant.repository.RestaurantRepository;
import me.tokyomap.dto.restaurant.RestaurantSearchRequestDto;
import me.tokyomap.dto.restaurant.RestaurantSearchResponseDto;
import me.tokyomap.service.RestaurantService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public List<RestaurantSearchResponseDto> searchRestaurants(RestaurantSearchRequestDto requestDto) {
        log.info("🔍 최종 category 조건: {}", requestDto.getCategory());
        log.info("📍 최종 city 조건: {}", requestDto.getCity());
        log.info("🕒 최종 openNow 조건: {}", requestDto.getOpenNow());


        List<Restaurant> result = restaurantRepository.searchByCondition(requestDto);

        return result.stream()
                .map(r -> RestaurantSearchResponseDto.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .address(r.getAddress())
                        .latitude(r.getLatitude())
                        .longitude(r.getLongitude())
                        .category(r.getCategory())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantSearchResponseDto getRestaurantById(Long id) {
        Restaurant r = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("음식점을 찾을 수 없습니다."));

        return RestaurantSearchResponseDto.builder()
                .id(r.getId())
                .name(r.getName())
                .address(r.getAddress())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .category(r.getCategory())
                .build();
    }
}
