package me.tokyomap.dto.restaurant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GooglePlaceRegisterRequestDto {

    @Schema(description = "Google Place ID", example = "ChIJN1t_tDeuEmsRUsoyG83frY4")
    private String placeId;

    @Schema(description = "음식점 이름", example = "스시야 스기모토")
    private String name;

    @Schema(description = "주소", example = "도쿄도 미나토구 롯폰기 1-1-1")
    private String address;

    @Schema(description = "평점", example = "4.5")
    private double rating;

    @Schema(description = "위도", example = "35.6895")
    private Double latitude;

    @Schema(description = "경도", example = "139.6917")
    private Double longitude;

}