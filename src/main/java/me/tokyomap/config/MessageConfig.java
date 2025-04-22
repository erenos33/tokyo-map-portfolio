package me.tokyomap.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class MessageConfig {

    // 다국어 메시지 파일(messages_ko.properties 등)을 읽기 위한 설정
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:messages"); // messages_ko.properties, messages_ja.properties 대상
        source.setDefaultEncoding("UTF-8");
        return source;
    }

    // 클라이언트의 Accept-Language 헤더로 Locale을 판단하는 기본 전략 설정
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.KOREA); // 기본은 한국어
        return resolver;
    }
}