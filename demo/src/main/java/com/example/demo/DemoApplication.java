package com.example.demo;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.idl.RuntimeWiring;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.security.Principal;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}

@Configuration
class SecurityConfiguration {

	@Bean
	MapReactiveUserDetailsService authentication() {
		return new MapReactiveUserDetailsService(
			User.withDefaultPasswordEncoder().username("jlong").password("pw").roles("USER").build()
		);
	}

	@Bean
	SecurityWebFilterChain authorization(ServerHttpSecurity http) {
		return http
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.httpBasic(Customizer.withDefaults())
			.authorizeExchange(ae -> ae
				.anyExchange().permitAll()
			)
			.build();
	}
}

@Configuration
class SimpleRuntimeWiringConfigurer implements RuntimeWiringConfigurer {


	@Override
	public void configure(RuntimeWiring.Builder builder) {
		builder
			.type("Query", b -> b.dataFetcher("hello", environment -> {
				var args = environment.getArguments();
				var name = args.get("name");
				return "Hello, " + name + "!";
			}))
			.build();
	}
}

@Controller
class HelloGraphqlController {

	@QueryMapping
	Mono<String> secureHello() {
		return ReactiveSecurityContextHolder
			.getContext()
			.map(SecurityContext::getAuthentication)
			.map(auth -> "Hello, " + auth.getName() + "!");
	}

}