package me.tokyomap.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * レストランがユーザーのお気に入りに登録されているかどうかを示すレスポンス
 */
@Getter
@AllArgsConstructor
public class FavoriteCheckResponseDto {

    @Schema(description = "当該レストランがユーザーのお気に入りに登録されているかを示すフラグ", example = "true")
    private boolean liked;
}
