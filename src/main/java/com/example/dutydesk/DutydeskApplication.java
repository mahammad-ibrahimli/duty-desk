package com.example.dutydesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.dutydesk", "com.example.dutydesk.securty"})
public class DutydeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(DutydeskApplication.class, args);
	}

}
