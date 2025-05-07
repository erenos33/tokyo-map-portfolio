package me.tokyomap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TokyomapApplication {

	public static void main(String[] args) {
		SpringApplication.run(TokyomapApplication.class, args);
	}

}
