package me.tokyomap.service;

import me.tokyomap.dto.user.UserRegisterRequestDto;

public interface UserService {
    void registerUser(UserRegisterRequestDto requestDto);

    boolean isEmailVerified(String email);
}
