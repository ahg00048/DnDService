package es.ujaen.ahg00048.microservice_image.rest;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.*;
import java.util.List;

import es.ujaen.ahg00048.microservice_image.service.ImageService;
import es.ujaen.ahg00048.microservice_image.rest.DTO.ImageWrap;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;


@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = es.ujaen.ahg00048.microservice_image.app.MicroserviceImageApplication.class)
@ContextConfiguration
@WebAppConfiguration
public class ImageControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc _mvc;

    @Autowired
    private ObjectMapper _oMapper;

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

    @BeforeEach
    public void setup() {
        _mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DirtiesContext
    public void imageManagementTest() throws Exception {
        String validUserId1 = "random1-id";
        String validUserId2 = "random2-id";

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
        _mvc.perform(
                multipart("/api/v1/images")
                .with(jwt().jwt((jwt -> jwt.subject(validUserId1))))
                .file(MockMPFileOverSized))
            .andExpect(status().isContentTooLarge());

        // Try to upload other kind of file
        _mvc.perform(
                multipart("/api/v1/images")
                .with(jwt().jwt((jwt -> jwt.subject(validUserId1))))
                .file(MockMPFileWrongFormat))
            .andExpect(status().isNotAcceptable());

        // Post image
        String imageId = _mvc.perform(
                multipart("/api/v1/images")
                .with(jwt().jwt((jwt -> jwt.subject(validUserId1))))
                .file(MockMPFileCorrect))
            .andReturn().getResponse().getContentAsString();

        String imageUrl = _mvc.perform(
                get("/api/v1/images/" + imageId)
                .with(jwt().jwt((jwt -> jwt.subject(validUserId1)))))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        Assertions.assertTrue(imageUrl.length() > 0);

        List<String> userImagesIds = _oMapper.readValue(_mvc.perform(
                get("/api/v1/images")
                .with(jwt().jwt((jwt -> jwt.subject(validUserId1)))))
            .andReturn().getResponse().getContentAsString(), new TypeReference<>() {});

        // Get user images Urls

        Assertions.assertEquals(userImagesIds.getFirst(), imageId);

        StringBuilder imageIdsStr = new StringBuilder();
        for (int i = 0; i < userImagesIds.size(); i++) {
            imageIdsStr.append(userImagesIds.get(i));
            if (i + 1 < userImagesIds.size())
                imageIdsStr.append(",");
        }

        // Get user images Urls
        List<ImageWrap> userImagesUrls = _oMapper.readValue(_mvc.perform(
                get("/api/v1/images?imagesIds=" + imageIdsStr)
                .with(jwt().jwt((jwt -> jwt.subject(validUserId1)))))
            .andReturn().getResponse().getContentAsString(), new TypeReference<>() {});

        Assertions.assertEquals(1, userImagesUrls.size());

        // Try to remove image from another user
        _mvc.perform(
                delete("/api/v1/images/" + imageId)
                .with(jwt().jwt((jwt -> jwt.subject(validUserId2)))))
            .andExpect(status().isForbidden());

        // Remove image
        _mvc.perform(
                delete("/api/v1/images/" + imageId)
                .with(jwt().jwt((jwt -> jwt.subject(validUserId1)))))
            .andExpect(status().isOk());

        // Try to remove image already deleted
        _mvc.perform(
                delete("/api/v1/images/" + imageId)
                .with(jwt().jwt((jwt -> jwt.subject(validUserId1)))))
            .andExpect(status().isNotFound());

        // Try to get deleted image
        _mvc.perform(
                get("/api/v1/images/" + imageId)
                .with(jwt().jwt((jwt -> jwt.subject(validUserId2)))))
            .andExpect(status().isNotFound());
    }
}
