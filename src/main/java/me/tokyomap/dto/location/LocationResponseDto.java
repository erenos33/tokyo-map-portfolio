package me.tokyomap.dto.location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 地域情報を返すレスポンスDTO
 * 国・都道府県・市区町村・番地・郵便番号などの情報を含む
 */
@Schema(description = "地域情報応答DTO")
@Getter
@AllArgsConstructor
public class LocationResponseDto {

    @Schema(description = "国名", example = "Japan")
    private String country;

    @Schema(description = "都道府県", example = "Tokyo")
    private String adminLevel;

    @Schema(description = "市区町村", example = "Shibuya City")
    private String adminLevel2;

    @Schema(description = "地域（町名など）", example = "Dogenzaka")
    private String locality;

    @Schema(description = "番地・丁目などの住所", example = "1 Chome-2-3")
    private String streetAddress;

    @Schema(description = "郵便番号", example = "150-0043")
    private String postalCode;
}
