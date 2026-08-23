package es.ujaen.ahg00048.microservice_lobby.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = es.ujaen.ahg00048.microservice_lobby.app.MicroserviceLobbyApplication.class)
@ActiveProfiles("test")

public class LobbyServiceTest {

    @Test
    @DirtiesContext
    public void exampleTest() {

    }
}