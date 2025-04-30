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

    @Schema(description = "음식점 이름", example = "Sushi Tokyo")
    private String name;

    @Schema(description = "주소", example = "Tokyo, Shibuya, ...")
    private String address;

    @Schema(description = "위도", example = "35.658034")
    private Double latitude;

    @Schema(description = "경도", example = "139.701636")
    private Double longitude;

    @Schema(description = "평점", example = "4.5")
    private Double rating;
}
