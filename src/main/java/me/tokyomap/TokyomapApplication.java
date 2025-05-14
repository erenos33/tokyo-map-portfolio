package me.tokyomap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * アプリケーションのエントリーポイント
 * Spring Bootを起動し、JPAの監査機能（作成日時・更新日時）を有効化
 */
@SpringBootApplication
@EnableJpaAuditing
public class TokyomapApplication {

	public static void main(String[] args) {
		SpringApplication.run(TokyomapApplication.class, args);
	}

}
