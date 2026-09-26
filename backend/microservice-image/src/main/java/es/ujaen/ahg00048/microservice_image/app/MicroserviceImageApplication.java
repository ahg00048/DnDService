package es.ujaen.ahg00048.microservice_image.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication
@ComponentScan({"es.ujaen.ahg00048.microservice_image.service",
		"es.ujaen.ahg00048.microservice_image.rest",
		"es.ujaen.ahg00048.microservice_image.config",
		"es.ujaen.ahg00048.microservice_image.repository.fileSystem"})
@EnableMongoRepositories("es.ujaen.ahg00048.microservice_image.repository.mongo")
public class MicroserviceImageApplication {
	public static void main(String[] args) {
		SpringApplication.run(MicroserviceImageApplication.class, args);
	}
}
