package es.ujaen.ahg00048.microservice_image.rest;

import es.ujaen.ahg00048.microservice_image.service.ImageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;


@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = es.ujaen.ahg00048.microservice_image.app.MicroserviceImageApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class ImageControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient _restClient;

    @Autowired
    private ImageService _imageService;


    @PostConstruct
    @AfterEach
    public void cleanUp() {
        try {
            _imageService.dropAllImages();
        } catch (RuntimeException e) {
            log.error("Unexpected exception at cleaning up test entity data");
        }
    }

    @Test
    public void imageManagementTest() {

    }
}
