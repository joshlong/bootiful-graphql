package com.example.demo;

import graphql.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.boot.test.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Locale;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureGraphQlTester
class DemoApplicationTests {


	@Autowired
	private WebGraphQlTester webGraphQlTester;


	@Test
	void contextLoads() {
		var query = """
			query {
					hello(name:"josh") {
							message
					}
			}
			""";
		Assertions.assertNotNull(this.webGraphQlTester);
		this.webGraphQlTester
			.query(query)
			.execute()
			.path("hello.message")
			.entity(String.class)
			.matches(g -> g.equalsIgnoreCase("hello, josh!"));
	}

}
