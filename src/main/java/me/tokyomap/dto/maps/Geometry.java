package me.tokyomap.dto.maps;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 地点のジオメトリ（緯度・経度などの位置情報）を表すDTO
 */
@Data
@Schema(description = "場所のジオメトリ情報")
public class Geometry {

    @Schema(description = "位置（緯度・経度）")
    private Location location;
}
