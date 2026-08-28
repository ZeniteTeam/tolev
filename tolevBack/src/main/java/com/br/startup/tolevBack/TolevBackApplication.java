package com.br.startup.tolevBack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TolevBackApplication {
	public static void main(String[] args) {
		SpringApplication.run(TolevBackApplication.class, args);
	}

}
