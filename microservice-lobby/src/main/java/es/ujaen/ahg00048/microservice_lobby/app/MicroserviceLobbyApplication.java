package es.ujaen.ahg00048.microservice_lobby.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan({"es.ujaen.ahg00048.microservice_lobby.service",
		"es.ujaen.ahg00048.microservice_lobby.rest"})
// @EnableMongoRepositories("es.ujaen.ahg00048.microservice_lobby.repository")
public class MicroserviceLobbyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceLobbyApplication.class, args);
	}

}
