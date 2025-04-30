package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.restaurant.repository.RestaurantRepository;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.dto.restaurant.GooglePlaceRegisterRequestDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

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

    @Transactional
    @Override
    public Long registerGooglePlace(GooglePlaceRegisterRequestDto dto, String userEmail) {

        restaurantRepository.findByPlaceId(dto.getPlaceId())
                .ifPresent(r -> { throw new CustomException(ErrorCode.RESTAURANT_ALREADY_REGISTERED); });

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Restaurant restaurant = Restaurant.builder()
                .placeId(dto.getPlaceId())
                .name(dto.getName())
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .rating(dto.getRating())
                .registeredBy(user)
                .build();

        restaurantRepository.save(restaurant);
        return restaurant.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<RestaurantSearchResponseDto> getMyRegisteredRestaurants(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return restaurantRepository.findByRegisteredBy(user, pageable)
                .map(RestaurantMapper::toDto);
    }

    @Transactional
    @Override
    public void deleteMyRestaurant(Long restaurantId, String userEmail) {
        Restaurant restaurant = EntityFinder.getRestaurantOrThrow(restaurantRepository, restaurantId);

        if (!restaurant.getRegisteredBy().getEmail().equals(userEmail)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED); // 내 음식점이 아니면 삭제 불가
        }

        restaurantRepository.delete(restaurant);
    }
}
