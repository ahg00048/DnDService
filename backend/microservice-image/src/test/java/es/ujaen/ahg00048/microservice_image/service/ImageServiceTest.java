package es.ujaen.ahg00048.microservice_image.service;

import es.ujaen.ahg00048.microservice_image.entity.image.Image;
import es.ujaen.ahg00048.microservice_image.entity.image.ImageType;
import es.ujaen.ahg00048.microservice_image.exception.ImageRegistrationException;
import es.ujaen.ahg00048.microservice_image.exception.InvalidOperationException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@SpringBootTest(classes = es.ujaen.ahg00048.microservice_image.app.MicroserviceImageApplication.class)
@ActiveProfiles("test")
public class ImageServiceTest {

    @Autowired
    private ImageService _imageService;

    @Autowired
    private MongoTemplate _mongoTemplate;


    @PostConstruct
    @AfterEach
    public void cleanUp() {
        try {
            _imageService.dropAllImages();
        } catch (IOException e) {
            log.error("Unexpected exception at cleaning up test entity data");
        }
    }

    @Test
    @DirtiesContext
    public void imageStorageTest() {
        String validUserId1 = "random1@gmail.com";
        String validUserId2 = "random2@gmail.com";

        Image image = new Image(validUserId1, "name.jpg", new byte[1], 1, 1, ImageType.JPG);

        for (int i = 0; i < _imageService.MAX_IMAGES_PER_USER(); i++) {
            image.setId(new ObjectId().toString());
            Assertions.assertDoesNotThrow(() -> _imageService.saveImage(validUserId1, image));
        }
        // Unable to add more than the amount specified
        image.setId(new ObjectId().toString());
        Assertions.assertThrows(InvalidOperationException.class, () -> _imageService.saveImage(validUserId1, image));

        List<Image> user1Images = new ArrayList<>();
        try {
            user1Images = _imageService.getUserImages(validUserId1);
        } catch (IOException e) {
            log.error("Unexpected IOException during test");
        }
        // Check that it is indeed the max amount
        Assertions.assertEquals(_imageService.MAX_IMAGES_PER_USER(), user1Images.size());

        String imageId = user1Images.getFirst().getId();

        // Get image by id
        Assertions.assertDoesNotThrow(() -> _imageService.getImage(imageId));

        // Try to delete image from another user
        Assertions.assertThrows(InvalidOperationException.class, () -> _imageService.deleteImage(validUserId2, imageId));

        // Delete image
        try {
            _imageService.deleteImage(validUserId1, imageId);
        } catch (IOException e) {
            log.error("Unexpected IOException during test");
        }
        // Try to delete image not saved
        Assertions.assertThrows(ImageRegistrationException.class, () -> _imageService.deleteImage(validUserId1, imageId));

        // Try to get image deleted
        Assertions.assertThrows(ImageRegistrationException.class, () -> _imageService.getImage(imageId));
    }
}
