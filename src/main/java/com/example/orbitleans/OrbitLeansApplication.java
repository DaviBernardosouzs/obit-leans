package com.example.orbitleans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OrbitLeansApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrbitLeansApplication.class, args);
	}

}
