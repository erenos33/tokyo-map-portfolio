package me.tokyomap.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("도쿄 맛집 포트폴리오 API")
                        .version("v1")
                        .description("도쿄 맛집 포트폴리오 프로젝트의 swagger문서입니다."));
    }
    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("public -api")
                .pathsToMatch("/api/**")
                .build();
    }
}
