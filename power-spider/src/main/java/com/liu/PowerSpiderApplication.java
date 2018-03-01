package com.liu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.liu.repository")
@EntityScan("com.liu.entity")
@SpringBootApplication
public class PowerSpiderApplication {

	public static void main(String[] args) {
		SpringApplication.run(PowerSpiderApplication.class, args);
	}
}
