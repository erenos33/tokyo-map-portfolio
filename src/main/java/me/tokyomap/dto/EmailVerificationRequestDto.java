package me.tokyomap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationRequestDto {

    @Schema(description = "사용자 이메일", example = "test@example.com")
    private String email;

    @Schema(description = "인증 코드", example = "1f3e5g")
    private String code;
}
