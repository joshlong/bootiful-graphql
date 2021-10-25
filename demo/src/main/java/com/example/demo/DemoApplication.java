package com.example.demo;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Log4j2
@SpringBootApplication
public class DemoApplication {


	@Bean
	ApplicationRunner applicationRunner() {
		return events -> log.info("hello, world!");
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

@Controller
class HelloGraphqlController {

	@QueryMapping
	String hello() {
		return "Hello, world";
	}

}