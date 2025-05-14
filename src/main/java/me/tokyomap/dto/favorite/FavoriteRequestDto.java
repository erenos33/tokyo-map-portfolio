package me.tokyomap.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * ユーザーがお気に入りに登録したいレストランのIDを受け取るリクエストDTO
 */
@Getter
public class FavoriteRequestDto {

    @Schema(description = "お気に入り登録対象のレストランID", example = "1")
    @NotNull
    private Long restaurantId;
}
