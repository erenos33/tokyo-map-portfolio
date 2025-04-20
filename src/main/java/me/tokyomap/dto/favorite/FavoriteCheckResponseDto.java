package me.tokyomap.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteCheckResponseDto {

    @Schema(description = "즐겨찾기 여부", example = "true")
    private boolean liked;
}
