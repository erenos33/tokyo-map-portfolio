package me.tokyomap.dto.restaurant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantSearchResponseDto {

    @Schema(description = "음식점 ID", example = "1")
    private Long id;

    @Schema(description = "이름", example = "스시진")
    private String name;

    @Schema(description = "주소", example = "도쿄 신주쿠구")
    private String address;

    @Schema(description = "위도", example = "35.6895")
    private Double latitude;

    @Schema(description = "경도", example = "139.6917")
    private Double longitude;

    @Schema(description = "카테고리", example = "스시")
    private String category;
}
