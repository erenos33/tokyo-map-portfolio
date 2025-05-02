package me.tokyomap.dto.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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

    // 필요한 경우 name, address 등도 추가 가능
}
