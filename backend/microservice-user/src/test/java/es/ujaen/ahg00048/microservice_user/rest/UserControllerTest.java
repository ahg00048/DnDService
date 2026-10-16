package es.ujaen.ahg00048.microservice_user.rest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;


@ActiveProfiles("test")
@SpringBootTest(classes = es.ujaen.ahg00048.microservice_user.app.MicroserviceUserApplication.class)
@ContextConfiguration
@WebAppConfiguration
public class UserControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc _mvc;

    @Autowired
    private ObjectMapper _oMapper;


    @BeforeEach
    public void setup() {
        _mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DirtiesContext
    public void removeUserTest()
    {

    }
}
