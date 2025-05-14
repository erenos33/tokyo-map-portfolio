package me.tokyomap.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * ユーザーのお気に入りに登録されたレストラン情報を返すDTO
 * レストランの基本情報（ID、名称、住所、評価など）を含む
 */
@Getter
@Builder
public class FavoriteRestaurantResponseDto {

    @Schema(description = "レストランID", example = "1")
    private Long restaurantId;

    @Schema(description = "レストラン名", example = "すし屋 すぎもと")
    private String name;

    @Schema(description = "住所", example = "東京都港区六本木1-1-1")
    private String address;

    @Schema(description = "平均評価", example = "4.5")
    private double averageRating;

    @Schema(description = "レビュー件数", example = "28")
    private int reviewCount;
}
