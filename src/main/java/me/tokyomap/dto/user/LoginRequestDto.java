package me.tokyomap.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {

    @Schema(description = "사용자 이메일 주소", example = "test@example.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "비밀번호 (8-20자)", example = "12345678")
    @NotBlank
    private String password;
}
