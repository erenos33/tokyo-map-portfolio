package me.tokyomap.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail);

    void verifyEmailCode(String email, String code);
}
