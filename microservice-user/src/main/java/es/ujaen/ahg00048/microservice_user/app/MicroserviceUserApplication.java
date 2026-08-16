package es.ujaen.ahg00048.microservice_user.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan({"es.ujaen.ahg00048.microservice_user.service",
		"es.ujaen.ahg00048.microservice_user.rest"})
@EnableMongoRepositories("es.ujaen.ahg00048.microservice_user.repository")
public class MicroserviceUserApplication {
	public static void main(String[] args) {
		SpringApplication.run(MicroserviceUserApplication.class, args);
	}
}
