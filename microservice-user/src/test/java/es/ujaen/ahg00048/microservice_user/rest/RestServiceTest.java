package es.ujaen.ahg00048.microservice_user.rest;

import es.ujaen.ahg00048.microservice_user.rest.DTO.JwtResponseDTO;
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
        String password = "seC8et$z";

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

        JwtResponseDTO resp = _restClient.get() // Correct login
                .uri("api/users/" + email + "?password=" + password)
                .exchange()
                .expectBody(JwtResponseDTO.class).returnResult().getResponseBody();

        userD = resp.user();

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
        String adminPwd = _env.getProperty("admin.pwd");

        UserDTO userD1 = new UserDTO("email1@gmail.com", "name1ee", "seC8et$z");
        UserDTO userD2 = new UserDTO("email2@gmail.com", "name2ee", "seC8et$z");

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

        JwtResponseDTO user1Resp = _restClient.get()
                .uri("api/users/" + userD1.email() + "?password=" + userD1.password())
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult().getResponseBody();

        JwtResponseDTO user2Resp = _restClient.get()
                .uri("api/users/" + userD2.email() + "?password=" + userD2.password())
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult().getResponseBody();

        JwtResponseDTO adminResp = _restClient.get()
                .uri("api/users/" + adminEmail + "?password=" + adminPwd)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult().getResponseBody();

        List<UserDTO> users = _restClient.get() // get users with admin
                .uri("api/users?id=" + adminEmail)
                .header("Authorization", "Bearer " + adminResp.access_token())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<UserDTO>>() {})
                .returnResult()
                .getResponseBody();

        _restClient.delete() // user trying removing other user
                .uri("api/users/" + userD1.email() + "?userToRemove=" + userD2.email())
                .header("Authorization", "Bearer " + user1Resp.access_token())
                .exchange()
                .expectStatus().isForbidden();

        _restClient.delete() // user removes his account
                .uri("api/users/" + userD1.email() + "?userToRemove=" + userD1.email())
                .header("Authorization", "Bearer " + user1Resp.access_token())
                .exchange()
                .expectStatus().isOk();

        _restClient.delete() // admin removing other user
                .uri("api/users/" + adminEmail + "?userToRemove=" + userD2.email())
                .header("Authorization", "Bearer " + adminResp.access_token())
                .exchange()
                .expectStatus().isOk();

        _restClient.delete() // admin removing other user unregistered
                .uri("api/users/" + adminEmail + "?userToRemove=" + userD2.email())
                .header("Authorization", "Bearer " + adminResp.access_token())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DirtiesContext
    public void getUsersTest()
    {
        String adminEmail = _env.getProperty("admin.email");
        String adminPwd = _env.getProperty("admin.pwd");

        JwtResponseDTO adminResp = _restClient.get()
                .uri("api/users/" + adminEmail + "?password=" + adminPwd)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult().getResponseBody();

        List<UserDTO> users = _restClient.get() // get users with admin
                .uri("api/users?id=" + adminEmail)
                .header("Authorization", "Bearer " + adminResp.access_token())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<UserDTO>>() {})
                .returnResult()
                .getResponseBody();

        Assertions.assertEquals(0, users.size());

        UserDTO userD = new UserDTO("email@gmail.com", "nameee", "seC8et$z");

        _restClient.post() // Add user
                .uri("api/users")
                .body(userD)
                .exchange()
                .expectStatus().isCreated();

        users =  _restClient.get() // get users with admin
                .uri("api/users?id=" + adminEmail)
                .header("Authorization", "Bearer " + adminResp.access_token())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<UserDTO>>() {})
                .returnResult()
                .getResponseBody();

        Assertions.assertEquals(1, users.size());

        JwtResponseDTO userResp = _restClient.get()
                .uri("api/users/" + userD.email() + "?password=" + userD.password())
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult().getResponseBody();

        _restClient.get() // get users with not admin
                .uri("api/users?id=" + userD.email())
                .header("Authorization", "Bearer " + userResp.access_token())
                .exchange()
                .expectStatus().isForbidden();

        _restClient.get() // get users with unregistered user
                .uri("api/users?id=" + "notfound@hotmail.com")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DirtiesContext
    public void changePasswordTest()
    {
        UserDTO userD = new UserDTO("email1@gmail.com", "name1ee", "seC8et$e");

        _restClient.put() // change password for unregistered user
                .uri("api/users/" + userD.email() + "?newPassword=" + "seC8et$e")
                .body(userD)
                .exchange()
                .expectStatus().isUnauthorized();

        _restClient.post() // Add user
                .uri("api/users")
                .body(userD)
                .exchange()
                .expectStatus().isCreated();

        JwtResponseDTO resp = _restClient.get()
                .uri("api/users/" + userD.email() + "?password=" + userD.password())
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult().getResponseBody();

        _restClient.put() // change password
                .uri("api/users/" + userD.email() + "?newPassword=" + "seC8et$z")
                .header("Authorization", "Bearer " + resp.access_token())
                .body(userD)
                .exchange()
                .expectStatus().isOk();

        _restClient.get()   // Login with old password
                .uri("api/users/" + userD.email() + "?password=" + userD.password())
                .header("Authorization", "Bearer " + resp.access_token())
                .exchange()
                .expectStatus().isUnauthorized();

        _restClient.get()   // Login with new password
                .uri("api/users/" + userD.email() + "?password=" + "seC8et$z")
                .header("Authorization", "Bearer " + resp.access_token())
                .exchange()
                .expectStatus().isOk();
    }
}
