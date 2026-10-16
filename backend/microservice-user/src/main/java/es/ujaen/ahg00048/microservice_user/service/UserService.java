package es.ujaen.ahg00048.microservice_user.service;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import es.ujaen.ahg00048.microservice_user.exception.*;


@Service
@Validated
public class UserService {
    @Autowired
    private KeycloakService _keycloakService;

    @Autowired
    private RabbitTemplate _rabbitTemplate;


    public List<UserRepresentation> getUsers() {
        return _keycloakService.getUsers();
    }

    public void removeUser(String userId, boolean isAdmin, String userIdToRemove)
            throws UserRegistrationException, UserAuthorizationException {
        if (!isAdmin && !userId.equals(userIdToRemove))
            throw new UserAuthorizationException();

        _keycloakService.deleteUser(userIdToRemove);

        _rabbitTemplate.send(new Message(userIdToRemove.getBytes()));
    }

    @Profile("test")
    public void createUser(String id, String username, String email) {
        UserRepresentation user = new UserRepresentation();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);

        _keycloakService.createUser(user);
    }
}
