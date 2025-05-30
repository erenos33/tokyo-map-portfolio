package me.tokyomap.mapper;

import me.tokyomap.domain.favorite.entity.Favorite;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.dto.favorite.FavoriteRestaurantResponseDto;

/**
 * FavoriteエンティティをFavoriteRestaurantResponseDtoに変換するマッパークラス
 */
public class FavoriteMapper {
    /**
     * お気に入り情報をレスポンスDTOに変換する
     */
    public static FavoriteRestaurantResponseDto toDto(Favorite favorite) {
        Restaurant r = favorite.getRestaurant();
        return FavoriteRestaurantResponseDto.builder()
                .restaurantId(r.getId())
                .name(r.getName())
                .address(r.getAddress())
                .averageRating(r.getRating() != null ? r.getRating() : 0.0)
                .reviewCount(r.getReviewCount() != null ? r.getReviewCount() : 0)
                .build();
    }
}