package es.ujaen.ahg00048.microservice_image.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest(classes = es.ujaen.ahg00048.microservice_image.app.MicroserviceImageApplication.class)
@Profile("test")
public class ImageServiceTest {

    @Autowired
    private ImageService _imageService;


    @Test
    public void imageStorageTest() {

    }

    @Test
    public void concurrencyTest() {

    }
}
