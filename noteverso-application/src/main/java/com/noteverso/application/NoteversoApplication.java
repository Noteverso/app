package com.noteverso.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.noteverso.note"})
public class NoteversoApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoteversoApplication.class, args);
	}

}

