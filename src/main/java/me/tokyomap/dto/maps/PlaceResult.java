package me.tokyomap.dto.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Google Place APIの検索結果1件分を表すDTO
 * 店舗名、住所、評価、位置情報、営業時間、価格帯などを含む
 */
@Data
@Schema(description = "Google Place検索結果")
public class PlaceResult {

    @Schema(description = "店舗名", example = "すし屋 すぎもと")
    private String name;

    @Schema(description = "住所（フォーマット済み）", example = "東京都港区六本木1-1-1")
    @JsonProperty("formatted_address")
    private String formattedAddress;

    @Schema(description = "平均評価", example = "4.5")
    private double rating;

    @Schema(description = "ジオメトリ情報（位置座標など）")
    private Geometry geometry;

    @Schema(description = "Google Place ID", example = "ChIJN1t_tDeuEmsRUsoyG83frY4")
    @JsonProperty("place_id")
    private String placeId;

    @JsonProperty("opening_hours")
    private OpeningHours openingHours;

    @JsonProperty("price_level")
    private Integer priceLevel;

    @JsonProperty("formatted_phone_number")
    private String phoneNumber;

}
