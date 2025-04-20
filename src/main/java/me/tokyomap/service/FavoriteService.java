package me.tokyomap.service;

import me.tokyomap.dto.favorite.FavoriteCheckResponseDto;
import me.tokyomap.dto.favorite.FavoriteRequestDto;
import me.tokyomap.dto.favorite.FavoriteRestaurantResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {

    void addFavorite(String email, FavoriteRequestDto requestDto);

    void removeFavorite(String email, FavoriteRequestDto requestDto);

    FavoriteCheckResponseDto checkFavorite(String email, Long restaurantId);

    Page<FavoriteRestaurantResponseDto> getMyFavorites(String email, Pageable pageable);
}
