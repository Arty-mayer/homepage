package com.homepage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.homepage")
public class HomePageApp {

	public static void main(String[] args) {
		SpringApplication.run(HomePageApp.class, args);
	}

}
