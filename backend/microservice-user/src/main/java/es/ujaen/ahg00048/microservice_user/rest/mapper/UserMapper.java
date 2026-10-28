package es.ujaen.ahg00048.microservice_user.rest.mapper;

import es.ujaen.ahg00048.microservice_user.rest.DTO.UserDTO;
import es.ujaen.ahg00048.microservice_user.rest.DTO.UserProfileDTO;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;


@Service
public class UserMapper {
    public UserDTO dto(UserRepresentation user) {
        return new UserDTO(user.getId(), user.getUsername());
    }

    public UserProfileDTO dto(UserRepresentation user, boolean isAdmin) {
        return new UserProfileDTO(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), isAdmin);
    }
}
