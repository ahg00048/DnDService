package es.ujaen.ahg00048.microservice_user.service;

import es.ujaen.ahg00048.microservice_user.exception.UserRegistrationException;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class KeycloakService {
    @Autowired
    private Keycloak _client;

    @Value("${keycloak.playground.realm}")
    private String _realm;


    public void createUser(UserRepresentation user) {
        try (Response resp = _client.realm(_realm).users().create(user)) {
            if (resp.getStatus() != 201)
                throw new UserRegistrationException();
        }
    }

    public void deleteUser(String userId)
            throws UserRegistrationException {
        try {
            _client.realm(_realm).users().get(userId).logout();
        } catch (UserRegistrationException e) {
            throw new UserRegistrationException();
        }

        try (Response resp = _client.realm(_realm).users().delete(userId)) {
            int st = resp.getStatus();
            if (resp.getStatus() != 204)
                throw new UserRegistrationException();
        }
    }

    public List<UserRepresentation> getUsers() {
        return _client.realm(_realm).users().list();
    }

    @PreDestroy
    private void freeResources() {
        _client.close();
    }
}
