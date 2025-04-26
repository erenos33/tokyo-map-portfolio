package me.tokyomap.dto.maps;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "위치 정보 (위도, 경도)")
public class Location {

    @Schema(description = "위도", example = "35.6895")
    private double lat;

    @Schema(description = "경도", example = "139.6917")
    private double lng;
}
