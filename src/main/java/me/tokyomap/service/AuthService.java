package me.tokyomap.service;

import me.tokyomap.dto.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(String email, String password);
}
