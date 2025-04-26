package me.tokyomap.dto.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Google Place 검색 결과")
public class PlaceResult {

    @Schema(description = "장소 이름", example = "스시야 스기모토")
    private String name;

    @Schema(description = "포맷된 주소", example = "도쿄도 미나토구 롯폰기 1-1-1")
    @JsonProperty("formatted_address")
    private String formattedAddress;

    @Schema(description = "평점", example = "4.5")
    private double rating;

    @Schema(description = "위치 정보 (geometry)")
    private Geometry geometry;

}
