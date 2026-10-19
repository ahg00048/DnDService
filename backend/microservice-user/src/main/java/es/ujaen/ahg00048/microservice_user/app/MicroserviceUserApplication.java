package es.ujaen.ahg00048.microservice_user.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan({"es.ujaen.ahg00048.microservice_user.service",
		"es.ujaen.ahg00048.microservice_user.rest",
		"es.ujaen.ahg00048.microservice_user.config"})
public class MicroserviceUserApplication {
	public static void main(String[] args) {
		SpringApplication.run(MicroserviceUserApplication.class, args);
	}
}
