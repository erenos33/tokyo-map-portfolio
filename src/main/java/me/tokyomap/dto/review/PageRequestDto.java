package me.tokyomap.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

public record PageRequestDto(
        @Schema(description = "페이지 번호", example = "0")
        int page,

        @Schema(description = "페이지 크기", example = "10")
        int size
) {}