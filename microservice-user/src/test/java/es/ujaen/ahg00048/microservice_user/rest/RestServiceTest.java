package es.ujaen.ahg00048.microservice_user.rest;


import org.junit.jupiter.api.Test;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = es.ujaen.ahg00048.microservice_user.app.MicroserviceUserApplication.class)
@ActiveProfiles("test")
public class RestServiceTest {
    int localPort;

    TestRestTemplate restTemplate;

    @Test
    public void exampleTest()
    {

    }
}
