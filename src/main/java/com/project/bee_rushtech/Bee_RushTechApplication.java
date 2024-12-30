package com.project.bee_rushtech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class Bee_RushTechApplication {
	public static void main(String[] args) {
		SpringApplication.run(Bee_RushTechApplication.class, args);

	}
}
