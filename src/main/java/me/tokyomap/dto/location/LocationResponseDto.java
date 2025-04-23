package me.tokyomap.dto.location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "지역 정보 응답 DTO")
@Getter
@AllArgsConstructor
public class LocationResponseDto {

    @Schema(description = "국가", example = "Japan")
    private String country;

    @Schema(description = "광역시/도", example = "Tokyo")
    private String adminLevel;

    @Schema(description = "시/구", example = "Shibuya City")
    private String adminLevel2;

    @Schema(description = "동/지역", example = "Dogenzaka")
    private String locality;

    @Schema(description = "도로명 주소", example = "1 Chome-2-3")
    private String streetAddress;

    @Schema(description = "우편번호", example = "150-0043")
    private String postalCode;
}
