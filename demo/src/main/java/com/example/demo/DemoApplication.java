package com.example.demo;

import graphql.schema.idl.RuntimeWiring;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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

record Greeting(String message) {
}

@Configuration
class SimpleRuntimeWiringConfigurer implements RuntimeWiringConfigurer {


	@Override
	public void configure(RuntimeWiring.Builder builder) {
		builder
			.type("Query", b -> b.dataFetcher("hello", environment -> {
				var args = environment.getArguments();
				var name = args.get("name");
				return new Greeting("Hello, " + name + "!");
			}))
			.build();
	}
}


record Customer(Integer id, Date enrollment, String name) {
}

@Controller
class HelloGraphqlController {

	private final ThreadLocal<SimpleDateFormat> df = new ThreadLocal<>();
	private final Map<Integer, Customer> db = new ConcurrentHashMap<>();
	private final AtomicInteger id = new AtomicInteger();

	HelloGraphqlController() {
		List.of("Yuxin", "Violetta", "Olga", "Madhura", "Josh", "Dave", "Yuxin", "Spencer")
			.forEach(this::buildCustomer);
	}

	@MutationMapping
	Mono<Customer> addCustomer(@Argument String name) {
		var key = this.buildCustomer(name);
		return Mono.just(this.db.get(key));
	}

	private int buildCustomer(String name) {
		var key = this.id.incrementAndGet();
		this.db.put(key, new Customer(key, new Date(), name));
		return key;
	}

	@SchemaMapping(typeName = "Customer")
	String date(Customer customer) {
		return buildIsoDateFormat().format(customer.enrollment());
	}

	@QueryMapping
	Flux<Customer> customers() {
		return Flux.fromIterable(this.db.values());
	}

	@QueryMapping
	Mono<String> secureHello() {
		return ReactiveSecurityContextHolder
			.getContext()
			.map(SecurityContext::getAuthentication)
			.map(auth -> "Hello, " + auth.getName() + "!");
	}


	private SimpleDateFormat buildIsoDateFormat() {
		if (this.df.get() == null) {
			var sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'") {
				{
					setTimeZone(TimeZone.getTimeZone("UTC"));
				}
			};

			this.df.set(sdf);
		}

		return this.df.get();
	}

	@SchemaMapping(typeName = "Customer")
	Flux<Order> orders(Customer customer) {
		var list = new ArrayList<Order>();
		var max = Math.random() * 100;
		for (var id = 1; id < max; id++) list.add(new Order(id, customer.id()));
		return Flux.fromIterable(list);
	}

}


record Order(Integer id, Integer customerId) {
}

