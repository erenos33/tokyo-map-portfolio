package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ページネーション情報を渡すためのリクエストDTO
 * ページ番号とページサイズを指定
 */
public record PageRequestDto(
        @Schema(description = "ページ番号", example = "0")
        int page,

        @Schema(description = "ページサイズ", example = "10")
        int size
) {}