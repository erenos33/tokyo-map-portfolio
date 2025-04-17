package me.tokyomap.dto.restaurant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RestaurantSearchRequestDto {

    @Schema(description = "음식 카테고리", example = "스시")
    private String category;

    @Schema(description = "도시 또는 구 이름", example = "신주쿠")
    private String city;

    @Schema(description = "현재 영업 중인 맛집만 필터링할지 여부", example = "true")
    private Boolean openNow;


}
