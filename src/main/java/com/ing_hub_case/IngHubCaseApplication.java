package com.ing_hub_case;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableFeignClients
public class IngHubCaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngHubCaseApplication.class, args);
	}

}
