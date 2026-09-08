package es.ujaen.ahg00048.microservice_lobby.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@ComponentScan(value = {"es.ujaen.ahg00048.microservice_lobby.service",
		"es.ujaen.ahg00048.microservice_lobby.rest",
		"es.ujaen.ahg00048.microservice_lobby.config",
		"es.ujaen.ahg00048.microservice_lobby.repository"},
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
				classes = {es.ujaen.ahg00048.microservice_lobby.repository.mongo.BoardRepository.class}))
@EntityScan
@EnableMongoRepositories("es.ujaen.ahg00048.microservice_lobby.repository.mongo")
public class MicroserviceLobbyApplication {
	public static void main(String[] args) {
		SpringApplication.run(MicroserviceLobbyApplication.class, args);
	}
}
