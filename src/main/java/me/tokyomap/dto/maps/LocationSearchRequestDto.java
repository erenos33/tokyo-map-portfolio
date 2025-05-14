package me.tokyomap.dto.maps;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 緯度・経度・半径・キーワードを指定して位置情報検索を行うためのリクエストDTO
 */
@Getter
@Setter
@Schema(description = "位置情報検索リクエストDTO")
public class LocationSearchRequestDto {

    @Schema(description = "緯度", example = "35.6895")
    private double lat;

    @Schema(description = "経度", example = "139.6917")
    private double lng;

    @Schema(description = "検索半径（メートル）", example = "1000")
    private int radius;

    @Schema(description = "検索キーワード", example = "ラーメン")
    private String keyword;
}
