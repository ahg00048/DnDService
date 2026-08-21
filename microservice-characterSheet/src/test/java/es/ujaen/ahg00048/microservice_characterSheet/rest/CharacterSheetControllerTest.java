package es.ujaen.ahg00048.microservice_characterSheet.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(classes = es.ujaen.ahg00048.microservice_characterSheet.app.MicroserviceCharacterSheetApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class CharacterSheetControllerTest {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private RestTestClient _restClient;


    @Test
    @DirtiesContext
    public void exampleTest() {

    }
}