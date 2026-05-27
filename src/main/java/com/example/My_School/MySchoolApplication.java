package com.example.My_School;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MySchoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(MySchoolApplication.class, args);
	}

}
