package es.ujaen.ahg00048.microservice_user.service;

import org.junit.jupiter.api.*;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import es.ujaen.ahg00048.microservice_user.exception.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;


@ActiveProfiles("test")
@SpringBootTest(classes = es.ujaen.ahg00048.microservice_user.app.MicroserviceUserApplication.class)
public class UserServiceTest {
    @Autowired
    private UserService _service;


    @Test
    @DirtiesContext
    public void serviceTest() {
        String id1 = "id1";
        String username1 = "randomusername1";
        String email1 = "random1@gmail.com";

        String id2 = "id2";
        String username2 = "randomusername2";
        String email2 = "random2@gmail.com";

        String id3 = "id3";
        String username3 = "randomusername3";
        String email3 = "random3@gmail.com";

        Assertions.assertThrows(UserRegistrationException.class, () -> _service.removeUser(id1, false, id1)); // User not registered

        _service.createUser(id1, username1, email1);
        _service.createUser(id2, username2, email2);
        _service.createUser(id3, username3, email3);

        List<UserRepresentation> users = _service.getUsers();
        final String Cid1 = users.stream().filter(u -> u.getUsername().equals(username1)).toList().getFirst().getId();
        final String Cid2 = users.stream().filter(u -> u.getUsername().equals(username2)).toList().getFirst().getId();
        final String Cid3 = users.stream().filter(u -> u.getUsername().equals(username3)).toList().getFirst().getId();

        Assertions.assertThrows(UserAuthorizationException.class, () -> _service.removeUser(Cid1, false, Cid2)); // User trying deleting other account

        Assertions.assertDoesNotThrow(() -> _service.removeUser(Cid1, false, Cid1)); // User removing their account

        Assertions.assertDoesNotThrow(() -> _service.removeUser(Cid2, true, Cid3)); // Admin user removing other account

        Assertions.assertThrows(UserRegistrationException.class, () -> _service.removeUser(Cid2, true, Cid3)); // User not registered

        _service.removeUser(Cid2, false, Cid2);
    }
}
