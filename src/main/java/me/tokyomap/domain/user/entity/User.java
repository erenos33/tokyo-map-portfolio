package me.tokyomap.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import me.tokyomap.domain.common.BaseTimeEntity;
import me.tokyomap.domain.user.role.UserRole;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@EqualsAndHashCode(of = "id")
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

    @Builder
    public User(String email, String password, String nickname, boolean emailVerified, UserRole role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.emailVerified = emailVerified;
        this.role = role;
    }
}
