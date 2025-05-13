package me.tokyomap.config;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * JWT認証をSwagger UIで使用するためのセキュリティスキーム設定
 */
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)

@Configuration
public class SwaggerConfig {

    /**
     * OpenAPIの基本情報（タイトル、バージョン、説明）を定義
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("グルメマップポートフォリオAPI")
                        .version("v1")
                        .description("グルメマップポートフォリオプロジェクトのSwaggerドキュメントです。"));
    }

    /**
     * APIグループ設定：/api/** パスにマッチするエンドポイントを対象とする
     */
    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("public -api")
                .pathsToMatch("/api/**")
                .build();
    }
}
