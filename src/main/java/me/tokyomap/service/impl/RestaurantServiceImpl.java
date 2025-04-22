package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.restaurant.repository.RestaurantRepository;
import me.tokyomap.dto.restaurant.RestaurantSearchRequestDto;
import me.tokyomap.dto.restaurant.RestaurantSearchResponseDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.mapper.RestaurantMapper;
import me.tokyomap.service.RestaurantService;
import me.tokyomap.util.EntityFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public Page<RestaurantSearchResponseDto> searchRestaurants(RestaurantSearchRequestDto requestDto, Pageable pageable) {

        Page<Restaurant> result = restaurantRepository.searchByCondition(requestDto, pageable);

        return result.map(RestaurantMapper::toDto);
    }

    @Override
    public RestaurantSearchResponseDto getRestaurantById(Long id) {
        Restaurant r = EntityFinder.getRestaurantOrThrow(restaurantRepository, id);

        return RestaurantMapper.toDto(r);
    }
}
