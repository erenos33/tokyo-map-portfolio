package me.tokyomap.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import me.tokyomap.domain.common.BaseTimeEntity;
import me.tokyomap.domain.user.role.UserRole;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = "password")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Size(min = 1, max = 10)
    private String nickname;

    private boolean emailVerified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private UserRole role;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpiresAt;

    @Builder
    public User(String email, String password, String nickname, boolean emailVerified, UserRole role, String verificationCode) {

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.emailVerified = emailVerified;
        this.role = role;
        this.verificationCode = verificationCode;
    }

    public void updateVerificationCode(String verificationCode, LocalDateTime expiresAt) {
        this.verificationCode = verificationCode;
        this.verificationCodeExpiresAt = expiresAt;
    }

    public void veriFyEmail() {

        this.emailVerified = true;
        this.verificationCode = null;//인증 성공 시 코드 삭제
    }
}
