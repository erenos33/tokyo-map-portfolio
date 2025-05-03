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

    @Schema(description = "영업시간", example = "월~일 11:00–22:00")
    private String openingHours;

    @Schema(description = "가격대", example = "₩₩₩")
    private String priceRange;

    @Schema(description = "전화번호", example = "03-1234-5678")
    private String phoneNumber;
}
