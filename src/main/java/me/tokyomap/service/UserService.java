package me.tokyomap.service;

import me.tokyomap.dto.user.UserRegisterRequestDto;
import org.springframework.stereotype.Service;

public interface UserService {
    void registerUser(UserRegisterRequestDto requestDto);
}
