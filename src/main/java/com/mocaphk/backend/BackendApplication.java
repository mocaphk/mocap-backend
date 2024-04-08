package com.mocaphk.backend;

import jakarta.servlet.Filter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	// https://stackoverflow.com/questions/48037601/lazyinitializationexception-with-graphql-spring
	@Bean
	public Filter openFilter() {
		return new OpenEntityManagerInViewFilter();
	}

}
