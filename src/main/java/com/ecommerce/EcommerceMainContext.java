package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EcommerceMainContext {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceMainContext.class, args);
	}

}
