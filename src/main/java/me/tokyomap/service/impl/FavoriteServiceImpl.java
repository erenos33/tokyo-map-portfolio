package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
import me.tokyomap.domain.favorite.entity.Favorite;
import me.tokyomap.domain.favorite.repository.FavoriteRepository;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.restaurant.repository.RestaurantRepository;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.dto.favorite.FavoriteCheckResponseDto;
import me.tokyomap.dto.favorite.FavoriteRequestDto;
import me.tokyomap.dto.favorite.FavoriteRestaurantResponseDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    @Transactional
    public void addFavorite(String email, FavoriteRequestDto requestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(requestDto.getRestaurantId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        if(favoriteRepository.existsByUserAndRestaurant(user, restaurant)){
            throw new CustomException(ErrorCode.ALREADY_FAVORITED);
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .restaurant(restaurant)
                .build();

        favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(String email, FavoriteRequestDto requestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(requestDto.getRestaurantId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        Favorite favorite = favoriteRepository.findByUserAndRestaurant(user, restaurant)
                .orElseThrow(() -> new CustomException(ErrorCode.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(favorite);
    }

    @Override
    public FavoriteCheckResponseDto checkFavorite(String email, Long restaurantId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));
        boolean liked = favoriteRepository.existsByUserAndRestaurant(user, restaurant);
        return new FavoriteCheckResponseDto(liked);

    }

    @Override
    public Page<FavoriteRestaurantResponseDto> getMyFavorites(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return favoriteRepository.findByUser(user, pageable)
                .map(favorite -> {
                    Restaurant restaurant = favorite.getRestaurant();
                    return FavoriteRestaurantResponseDto.builder()
                            .restaurantId(restaurant.getId())
                            .name(restaurant.getName())
                            .address(restaurant.getAddress())
                            .averageRating(restaurant.getRating())
                            .reviewCount(restaurant.getReviewCount())
                            .build();
                });
    }
}
