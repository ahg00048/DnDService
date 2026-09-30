package es.ujaen.ahg00048.microservice_image.rest;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.client.RestTestClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import java.io.*;
import java.util.List;

import es.ujaen.ahg00048.microservice_image.service.ImageService;
import es.ujaen.ahg00048.microservice_image.rest.DTO.ImageWrap;


@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = es.ujaen.ahg00048.microservice_image.app.MicroserviceImageApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@AutoConfigureMockMvc
public class ImageControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient _restClient;

    @Autowired
    private MockMvcTester _mvcTester;

    @Autowired
    private ImageService _imageService;

    @Value("${app.sampleImagePath}")
    private String _sampleImagePath;

    @Value("${app.sampleOverSizedImagePath}")
    private String _sampleOverSizedImagePath;

    @Value("${app.sampleFilePath}")
    private String _sampleFilePath;


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
    public void imageManagementTest() throws IOException {
        String validUserId1 = "random1@gmail.com";
        String validUserId2 = "random2@gmail.com";

        File file1 = new File(_sampleImagePath);
        File file2 = new File(_sampleOverSizedImagePath);
        File file3 = new File(_sampleFilePath);

        byte[] data = null;

        try(InputStream iStream = new FileInputStream(file1)) {
            data = iStream.readAllBytes();
        } catch (Exception e) {
            log.error("Sample image not found.");
        }

        MockMultipartFile MockMPFileCorrect = new MockMultipartFile("image",
                file1.getName(),
                MediaType.IMAGE_PNG.toString(),
                data);

        try(InputStream iStream = new FileInputStream(file2)) {
            data = iStream.readAllBytes();
        } catch (Exception e) {
            log.error("Sample image not found.");
        }

        MockMultipartFile MockMPFileOverSized = new MockMultipartFile("image",
                file2.getName(),
                MediaType.IMAGE_PNG.toString(),
                data);


        try(InputStream iStream = new FileInputStream(file3)) {
            data = iStream.readAllBytes();
        } catch (Exception e) {
            log.error("Sample image not found.");
        }

        MockMultipartFile MockMPFileWrongFormat = new MockMultipartFile("image",
                file3.getName(),
                MediaType.TEXT_PLAIN_UTF_8.toString(),
                data);

        // Try to upload other oversized image
        assertThat(_mvcTester.perform(
                multipart("/api/v1/images?userId=" + validUserId1)
                .file(MockMPFileOverSized)
                )).hasStatus(HttpStatus.CONTENT_TOO_LARGE);

        // Try to upload other kind of file
        assertThat(_mvcTester.perform(
                multipart("/api/v1/images?userId=" + validUserId1)
                .file(MockMPFileWrongFormat)
        )).hasStatus(HttpStatus.NOT_ACCEPTABLE);

        // Post image
        String imageId = _mvcTester.perform(
                multipart("/api/v1/images?userId=" + validUserId1)
                        .file(MockMPFileCorrect)).getResponse().getContentAsString();

        String imageUrl = _restClient.get().uri("/api/v1/images/" + imageId + "?userId=" + validUserId1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();


        // Get image through generated url
        Resource imageFile = _restClient.get().uri(imageUrl)
                .exchange()
                .expectBody(Resource.class)
                .returnResult().getResponseBody();

        Assertions.assertTrue(imageFile.contentLength() > 0);

        // Get user images Urls
        List<String> userImagesIds = _restClient.get().uri("/api/v1/images?userId=" + validUserId1)
                .exchange()
                .expectBody(new ParameterizedTypeReference<List<String>>() {})
                .returnResult().getResponseBody();

        Assertions.assertEquals(userImagesIds.getFirst(), imageId);

        StringBuilder imageIdsStr = new StringBuilder();
        for (int i = 0; i < userImagesIds.size(); i++) {
            imageIdsStr.append(userImagesIds.get(i));
            if (i + 1 < userImagesIds.size())
                imageIdsStr.append(",");
        }

        // Get user images Urls
        List<ImageWrap> userImagesUrls = _restClient.get().uri("/api/v1/images?imagesIds=" + imageIdsStr.toString())
                .exchange()
                .expectBody(new ParameterizedTypeReference<List<ImageWrap>>() {})
                .returnResult().getResponseBody();

        Assertions.assertEquals(1, userImagesUrls.size());

        // Bad request on get images
        _restClient.get().uri("/api/v1/images")
                .exchange()
                .expectStatus().isBadRequest();

        // Try to remove image from another user
        _restClient.delete().uri("/api/v1/images/" + imageId + "?userId=" + validUserId2)
                .exchange()
                .expectStatus().isForbidden();

        // Remove image
        _restClient.delete().uri("/api/v1/images/" + imageId + "?userId=" + validUserId1)
                .exchange()
                .expectStatus().isOk();

        // Try to remove image already deleted
        _restClient.delete().uri("/api/v1/images/" + imageId + "?userId=" + validUserId1)
                .exchange()
                .expectStatus().isNotFound();

        // Try to get deleted image
        _restClient.get().uri("/api/v1/images/" + imageId)
                .exchange()
                .expectStatus().isNotFound();
    }
}
