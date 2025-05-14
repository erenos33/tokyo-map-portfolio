package me.tokyomap.util;

import java.util.Base64;

/**
 * JWT用のシークレットキーをBase64形式にエンコードするユーティリティクラス
 * 一時的な開発用途で使用（実行後、コンソールに出力される）
 */
public class JwtSecretEncoder {

    public static void main(String[] args) {
        String secret = "my-very-secret-key-for-jwt";
        String encoded = Base64.getEncoder().encodeToString(secret.getBytes());
        System.out.println("Base64 encoded string: " + encoded);
    }
}
