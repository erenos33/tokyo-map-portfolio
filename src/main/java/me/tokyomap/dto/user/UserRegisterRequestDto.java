package me.tokyomap.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequestDto {

    @Email(message = "유효한 이메일 주소를 입력해주세요")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20)
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 1, max = 10)
    private String nickname;
}
