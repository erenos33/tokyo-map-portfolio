package me.tokyomap.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class FavoriteRequestDto {

    @Schema(description = "즐겨찾기할 음식점 ID", example = "1")
    @NotNull
    private Long restaurantId;
}
