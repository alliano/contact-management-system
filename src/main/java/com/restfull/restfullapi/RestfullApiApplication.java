package com.restfull.restfullapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.restfull.restfullapi.controllers.ErrorController;

@SpringBootApplication
@Import(value = {
	ErrorController.class
})
public class RestfullApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestfullApiApplication.class, args);
	}

}
