package me.tokyomap.dto.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Google Place APIから取得される詳細情報のDTO
 * 電話番号、価格帯、営業時間、住所などを含む
 */
@Data
public class GooglePlaceDetailResponseDto {

    @JsonProperty("formatted_phone_number")
    private String phoneNumber;

    @JsonProperty("price_level")
    private Integer priceLevel;

    @JsonProperty("opening_hours")
    private OpeningHours openingHours;

    @JsonProperty("formatted_address")
    private String formattedAddress;
}
