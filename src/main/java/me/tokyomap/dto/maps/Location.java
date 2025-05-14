package me.tokyomap.dto.maps;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 緯度・経度などの位置情報を表すDTO
 */
@Data
@Schema(description = "位置情報（緯度・経度）")
public class Location {

    @Schema(description = "緯度", example = "35.6895")
    private double lat;

    @Schema(description = "経度", example = "139.6917")
    private double lng;
}
