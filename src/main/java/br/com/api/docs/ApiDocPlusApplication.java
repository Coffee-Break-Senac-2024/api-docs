package br.com.api.docs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
public class ApiDocPlusApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiDocPlusApplication.class, args);
	}

}
