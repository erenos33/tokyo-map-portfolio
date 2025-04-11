package me.tokyomap.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class UserRegisterRequestDto {

    @Schema(description = "사용자 이메일 주소", example = "example@example.com")
    @Email(message = "유효한 이메일 주소를 입력해주세요")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "비밀번호 (8~20자)", example = "12345678")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20)
    private String password;

    @Schema(description = "닉네임 (1~10자)", example = "도쿄맛집러")
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 1, max = 10)
    private String nickname;
}
