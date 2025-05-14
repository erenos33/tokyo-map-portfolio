package me.tokyomap.dto.restaurant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Google Mapsの検索結果からレストランを登録するためのリクエストDTO
 * 必要な項目：Place ID、店舗名、住所、評価、位置情報、営業時間、価格帯、電話番号など
 */
@Getter @Setter
public class GooglePlaceRegisterRequestDto {

    @Schema(description = "Google Place ID", example = "ChIJN1t_tDeuEmsRUsoyG83frY4")
    private String placeId;

    @Schema(description = "店舗名", example = "すし屋 すぎもと")
    private String name;

    @Schema(description = "住所", example = "東京都港区六本木1-1-1")
    private String address;

    @Schema(description = "平均評価", example = "4.5")
    private double rating;

    @Schema(description = "緯度", example = "35.6895")
    private Double latitude;

    @Schema(description = "経度", example = "139.6917")
    private Double longitude;

    @Schema(description = "営業時間", example = "月〜日 11:00–22:00")
    private String openingHours;

    @Schema(description = "価格帯", example = "₩₩₩")
    private String priceRange;

    @Schema(description = "電話番号", example = "03-1234-5678")
    private String phoneNumber;

}