package es.ujaen.ahg00048.microservice_user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import es.ujaen.ahg00048.microservice_user.entity.User;
import es.ujaen.ahg00048.microservice_user.exception.*;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService _service;

    @Test
    @DirtiesContext
    public void registerTest() {
        User user = new User("valid@gmail.com", "name1", "secret");

        Assertions.assertDoesNotThrow(() -> _service.addUser(user)); // Register valid user

        Assertions.assertThrows(UserRegistrationException.class, () -> _service.addUser(user)); // Register same valid user
    }

    @Test
    @DirtiesContext
    public void loginTest() {
        User user = new User("valid@gmail.com", "name1", "secret");
        final String userEmail = user.getEmail();
        final String userPassword = user.getPassword();

        Assertions.assertThrows(UserRegistrationException.class, () -> _service.login(userEmail, userPassword)); // User not registered

        user = _service.addUser(user);

        Assertions.assertThrows(UserAuthenticationException.class, () -> _service.login(userEmail, "otherPassword")); // Wrong password

        Assertions.assertDoesNotThrow(() -> _service.login(userEmail, userPassword));
    }

    @Test
    @DirtiesContext
    public void removalTest() {
        final User user1 = new User("valid@gmail.com", "name1", "secret");
        final User user2 = new User("valid2@gmail.com", "name1", "secret");

        final String user1Email = user1.getEmail();
        final String user2Email = user2.getEmail();

        Assertions.assertThrows(UserRegistrationException.class, () -> _service.removeUser(user1, user1Email)); // User not registered

        final User user_1f = _service.addUser(user1);
        final User user_2f = _service.addUser(user2);

        Assertions.assertThrows(UserAuthorizationException.class, () -> _service.removeUser(user_1f, user2Email)); // User trying deleting other account

        Assertions.assertDoesNotThrow(() -> _service.removeUser(user_1f, user1Email)); // User removing their account

        Assertions.assertDoesNotThrow(() -> _service.removeUser(_service.admin, user2Email)); // Admin removing other account

        Assertions.assertThrows(UserRegistrationException.class, () -> _service.removeUser(_service.admin, user1Email)); // User not registered
    }
}
