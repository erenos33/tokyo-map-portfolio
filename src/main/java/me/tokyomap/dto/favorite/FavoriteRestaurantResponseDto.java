package me.tokyomap.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteRestaurantResponseDto {

    @Schema(description = "음식점 ID", example = "1")
    private Long restaurantId;

    @Schema(description = "음식점 이름", example = "스시야 스기모토")
    private String name;

    @Schema(description = "주소", example = "도쿄도 미나토구 롯폰기 1-1-1")
    private String address;

    @Schema(description = "평점", example = "4.5")
    private double averageRating;

    @Schema(description = "리뷰 수", example = "28")
    private int reviewCount;
}
