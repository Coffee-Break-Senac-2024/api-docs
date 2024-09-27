package br.com.api.docs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiDocPlusApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiDocPlusApplication.class, args);
	}

}
