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
import me.tokyomap.mapper.FavoriteMapper;
import me.tokyomap.service.FavoriteService;
import me.tokyomap.util.EntityFinder;
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
        User user = EntityFinder.getUserOrThrow(userRepository, email);

        Restaurant restaurant = EntityFinder.getRestaurantOrThrow(restaurantRepository, requestDto.getRestaurantId());

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
        User user = EntityFinder.getUserOrThrow(userRepository, email);

        Restaurant restaurant = EntityFinder.getRestaurantOrThrow(restaurantRepository, requestDto.getRestaurantId());

        Favorite favorite = favoriteRepository.findByUserAndRestaurant(user, restaurant)
                .orElseThrow(() -> new CustomException(ErrorCode.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(favorite);
    }

    @Override
    public FavoriteCheckResponseDto checkFavorite(String email, Long restaurantId) {
        User user = EntityFinder.getUserOrThrow(userRepository, email);

        Restaurant restaurant = EntityFinder.getRestaurantOrThrow(restaurantRepository, restaurantId);

        boolean liked = favoriteRepository.existsByUserAndRestaurant(user, restaurant);
        return new FavoriteCheckResponseDto(liked);

    }

    @Override
    public Page<FavoriteRestaurantResponseDto> getMyFavorites(String email, Pageable pageable) {
        User user = EntityFinder.getUserOrThrow(userRepository, email);

        return favoriteRepository.findByUser(user, pageable)
                .map(FavoriteMapper::toDto);
    }
}
