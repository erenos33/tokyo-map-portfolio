package me.tokyomap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    /**
     * CORSの設定クラス
     * ReactフロントエンドとSpringバックエンド間の通信を許可
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 全てのパスを対象
                        .allowedOrigins("https://tokyo-map-portfolio.vercel.app") // 許可するフロントエンドのURL
                        .allowedMethods("*") // 全てのHTTPメソッドを許可
                        .allowedHeaders("*") // 全てのヘッダーを許可
                        .allowCredentials(true); // 認証情報を許可（Cookieなど）
            }
        };
    }
}
