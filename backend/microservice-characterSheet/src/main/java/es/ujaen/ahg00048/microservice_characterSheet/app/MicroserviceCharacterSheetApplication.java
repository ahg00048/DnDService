package es.ujaen.ahg00048.microservice_characterSheet.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan({"es.ujaen.ahg00048.microservice_characterSheet.service",
		"es.ujaen.ahg00048.microservice_characterSheet.service",
		"es.ujaen.ahg00048.microservice_characterSheet.rest"})
@EnableMongoRepositories("es.ujaen.ahg00048.microservice_characterSheet.repository")
public class MicroserviceCharacterSheetApplication {
	public static void main(String[] args) {
		SpringApplication.run(MicroserviceCharacterSheetApplication.class, args);
	}
}
