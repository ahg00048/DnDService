package es.ujaen.ahg00048.microservice_user.rest;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import java.util.List;

import es.ujaen.ahg00048.microservice_user.rest.DTO.UserDTO;


@SpringBootTest(classes = es.ujaen.ahg00048.microservice_user.app.MicroserviceUserApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class RestServiceTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private RestTestClient _restClient;

    @Autowired
    private Environment _env;

    @Autowired
    private MongoTemplate _mongoTemplate;

    @AfterEach
    @PostConstruct
    public void cleanUp() {
        _mongoTemplate.getDb().drop();
    }

    @Test
    @DirtiesContext
    public void loginTest()
    {
        String email = "email@gmail.com";
        String password = "secretee";

        _restClient.get()   // Login of unregistered user
                .uri("api/users/" + email + "?password=" + password)
                .exchange()
                .expectStatus().isNotFound();

        UserDTO userD = new UserDTO(email, "nameee", password);

        _restClient.post() // Add user
                .uri("api/users")
                .body(userD)
                .exchange()
                .expectStatus().isCreated();

        _restClient.get() // Login of without password
                .uri("api/users/" + email)
                .exchange()
                .expectStatus().isBadRequest();

        userD = _restClient.get() // Correct login
                .uri("api/users/" + email + "?password=" + password)
                .exchange()
                .expectBody(UserDTO.class).returnResult().getResponseBody();

        _restClient.post() // Add user already registered
                .uri("api/users")
                .body(new UserDTO(email, "nameee", password))
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    @DirtiesContext
    public void removeUserTest()
    {
        String adminEmail = _env.getProperty("admin.email");

        UserDTO userD1 = new UserDTO("email1@gmail.com", "name1", "secret");
        UserDTO userD2 = new UserDTO("email2@gmail.com", "name2", "secret");

        _restClient.post() // Add user
                .uri("api/users")
                .body(userD1)
                .exchange()
                .expectStatus().isCreated();

        _restClient.post() // Add user
                .uri("api/users")
                .body(userD2)
                .exchange()
                .expectStatus().isCreated();

        List<UserDTO> users = _restClient.get() // get users with admin
                .uri("api/users?id=" + adminEmail)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<UserDTO>>() {})
                .returnResult()
                .getResponseBody();

        _restClient.delete() // user trying removing other user
                .uri("api/users/" + userD1.email() + "?userToRemove=" + userD2.email())
                .exchange()
                .expectStatus().isForbidden();

        _restClient.delete() // user removes his account
                .uri("api/users/" + userD1.email() + "?userToRemove=" + userD1.email())
                .exchange()
                .expectStatus().isOk();

        _restClient.delete() // admin removing other user
                .uri("api/users/" + adminEmail + "?userToRemove=" + userD2.email())
                .exchange()
                .expectStatus().isOk();

        _restClient.delete() // admin removing other user unregistered
                .uri("api/users/" + adminEmail + "?userToRemove=" + userD2.email())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DirtiesContext
    public void getUsersTest()
    {
        String email = _env.getProperty("admin.email");

        List<UserDTO> users = _restClient.get() // get users with admin
                .uri("api/users?id=" + email)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<UserDTO>>() {})
                .returnResult()
                .getResponseBody();

        Assertions.assertEquals(0, users.size());

        UserDTO userD = new UserDTO("email@gmail.com", "name", "secret");

        _restClient.post() // Add user
                .uri("api/users")
                .body(userD)
                .exchange()
                .expectStatus().isCreated();

        users =  _restClient.get() // get users with admin
                .uri("api/users?id=" + email)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<UserDTO>>() {})
                .returnResult()
                .getResponseBody();

        Assertions.assertEquals(1, users.size());

        _restClient.get() // get users with not admin
                .uri("api/users?id=" + userD.email())
                .exchange()
                .expectStatus().isForbidden();

        _restClient.get() // get users with unregistered user
                .uri("api/users?id=" + "notfound@hotmail.com")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DirtiesContext
    public void changePasswordTest()
    {
        UserDTO userD = new UserDTO("email1@gmail.com", "name1", "secret");

        _restClient.put() // change password for unregistered user
                .uri("api/users/" + userD.email() + "?newPassword=" + "secret2")
                .body(userD)
                .exchange()
                .expectStatus().isNotFound();

        _restClient.post() // Add user
                .uri("api/users")
                .body(userD)
                .exchange()
                .expectStatus().isCreated();

        _restClient.get()   // Login
                .uri("api/users/" + userD.email() + "?password=" + userD.password())
                .exchange()
                .expectStatus().isOk();

        _restClient.put() // change password
                .uri("api/users/" + userD.email() + "?newPassword=" + "secret2")
                .body(userD)
                .exchange()
                .expectStatus().isOk();

        _restClient.get()   // Login with old password
                .uri("api/users/" + userD.email() + "?password=" + userD.password())
                .exchange()
                .expectStatus().isUnauthorized();

        _restClient.get()   // Login with new password
                .uri("api/users/" + userD.email() + "?password=" + "secret2")
                .exchange()
                .expectStatus().isOk();
    }
}
