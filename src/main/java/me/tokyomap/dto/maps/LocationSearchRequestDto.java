package me.tokyomap.dto.maps;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "위치 기반 검색 요청 DTO")
public class LocationSearchRequestDto {

    @Schema(description = "위도", example = "35.6895")
    private double lat;

    @Schema(description = "경도", example = "139.6917")
    private double lng;

    @Schema(description = "검색 반경 (미터 단위)", example = "1000")
    private int radius;

    @Schema(description = "검색 키워드", example = "라멘")
    private String keyword;
}
