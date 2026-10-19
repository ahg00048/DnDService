package es.ujaen.ahg00048.microservice_user.rest.mapper;

import es.ujaen.ahg00048.microservice_user.rest.DTO.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;


@Service
public class UserMapper {
    public UserDTO dto(UserRepresentation user) {
        return new UserDTO(user.getId(), user.getUsername());
    }
}
