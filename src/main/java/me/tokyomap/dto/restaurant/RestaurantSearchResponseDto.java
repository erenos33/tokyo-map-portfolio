package me.tokyomap.dto.restaurant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * レストラン検索結果1件分のレスポンスDTO
 * 店舗の基本情報（ID、名称、住所、位置、評価、営業時間など）を含む
 */
@Getter
@Builder
@AllArgsConstructor
public class RestaurantSearchResponseDto {

    @Schema(description = "レストランID", example = "1")
    private Long id;

    @Schema(description = "店舗名", example = "Sushi Tokyo")
    private String name;

    @Schema(description = "住所", example = "Tokyo, Shibuya, ...")
    private String address;

    @Schema(description = "緯度", example = "35.658034")
    private Double latitude;

    @Schema(description = "経度", example = "139.701636")
    private Double longitude;

    @Schema(description = "平均評価", example = "4.5")
    private Double rating;

    @Schema(description = "営業時間", example = "월~일 11:00–22:00")
    private String openingHours;

    @Schema(description = "価格帯", example = "₩₩₩")
    private String priceRange;

    @Schema(description = "電話番号", example = "03-1234-5678")
    private String phoneNumber;
}
