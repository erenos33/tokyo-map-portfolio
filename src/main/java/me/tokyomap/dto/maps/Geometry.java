package me.tokyomap.dto.maps;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "장소의 지오메트리 정보")
public class Geometry {

    @Schema(description = "위치 (위도/경도)")
    private Location location;
}
