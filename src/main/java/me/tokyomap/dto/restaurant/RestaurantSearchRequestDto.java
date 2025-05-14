package me.tokyomap.dto.restaurant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * レストラン検索のためのリクエストDTO
 * カテゴリ、都市名、現在営業中かどうかのフィルターを含む
 */
@Getter
@Setter
@AllArgsConstructor
public class RestaurantSearchRequestDto {

    @Schema(description = "料理カテゴリ", example = "寿司")
    private String category;

    @Schema(description = "都市または区の名称", example = "新宿")
    private String city;

    @Schema(description = "現在営業中の店舗のみを対象とするかを示すフラグ", example = "true")
    private Boolean openNow;


}
