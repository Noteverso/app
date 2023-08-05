package com.noteverso.application;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.noteverso"})
@MapperScan("com.noteverso.*.dao")
public class NoteversoApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoteversoApplication.class, args);
	}

}

