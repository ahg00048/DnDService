package es.ujaen.ahg00048.microservice_user.rest.mapper;

import es.ujaen.ahg00048.microservice_user.entity.User;
import es.ujaen.ahg00048.microservice_user.rest.DTO.UserDTO;
import org.springframework.stereotype.Service;


@Service
public class UserMapper {
    public User entity(UserDTO user) {
        return new User(user.email(), user.name(), "");
    }

    public User newEntity(UserDTO user) {
        return new User(user.email(), user.name(), user.password()); // toda la logica de encriptacion de contraseñas
    }

    public UserDTO dto(User user) {
        return new UserDTO(user.getEmail(), user.getName(), "");
    }
}
