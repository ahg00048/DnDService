package es.ujaen.ahg00048.microservice_image.service;

import es.ujaen.ahg00048.microservice_image.entity.image.Image;
import es.ujaen.ahg00048.microservice_image.exception.ImageRegistrationException;
import es.ujaen.ahg00048.microservice_image.exception.InvalidOperationException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import org.xmlunit.builder.Input;

import javax.print.attribute.standard.Media;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@SpringBootTest(classes = es.ujaen.ahg00048.microservice_image.app.MicroserviceImageApplication.class)
@ActiveProfiles("test")
public class ImageServiceTest {

    @Autowired
    private ImageService _imageService;

    @Value("${app.sampleImagePath}")
    private String _sampleImagePath;

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
    @DirtiesContext
    public void imageStorageTest() {
        String validUserId1 = "random1@gmail.com";
        String validUserId2 = "random2@gmail.com";


        File file = new File(_sampleImagePath);

        byte[] data = null;

        try(InputStream iStream = new FileInputStream(file)) {
            data = iStream.readAllBytes();
        } catch (Exception e) {
            log.error("Sample image not found.");
        }


        MockMultipartFile MockMPFile = new MockMultipartFile("image",
                file.getName(),
                MediaType.IMAGE_PNG.toString(),
                data);


        for (int i = 0; i < _imageService.getMAX_IMAGES_PER_USER(); i++) {
            Assertions.assertDoesNotThrow(() -> _imageService.saveImage(validUserId1, MockMPFile));
        }
        // Unable to add more than the amount specified
        Assertions.assertThrows(InvalidOperationException.class, () -> _imageService.saveImage(validUserId1, MockMPFile));

        List<Image> user1Images = new ArrayList<>();
        try {
            user1Images = _imageService.getUserImages(validUserId1);
        } catch (RuntimeException e) {
            log.error("Unexpected IOException during test");
        }
        // Check that it is indeed the max amount
        Assertions.assertEquals(_imageService.getMAX_IMAGES_PER_USER(), user1Images.size());

        String imageId = user1Images.getFirst().getId();

        // Get image by id
        Assertions.assertDoesNotThrow(() -> _imageService.getImage(imageId));

        // Try to delete image from another user
        Assertions.assertThrows(InvalidOperationException.class, () -> _imageService.deleteImage(validUserId2, imageId));

        // Delete image
        try {
            _imageService.deleteImage(validUserId1, imageId);
        } catch (RuntimeException e) {
            log.error("Unexpected IOException during test");
        }
        // Try to delete image not saved
        Assertions.assertThrows(ImageRegistrationException.class, () -> _imageService.deleteImage(validUserId1, imageId));

        // Try to get image deleted
        Assertions.assertThrows(ImageRegistrationException.class, () -> _imageService.getImage(imageId));
    }
}
