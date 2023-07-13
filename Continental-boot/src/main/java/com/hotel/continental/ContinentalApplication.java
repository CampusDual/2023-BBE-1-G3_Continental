package com.hotel.continental;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = false)
public class ContinentalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContinentalApplication.class, args);
	}
}
