package com.example.mutsamarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MutsamarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MutsamarketApplication.class, args);
	}

}
