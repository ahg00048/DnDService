package es.ujaen.ahg00048.microservice_user.rest.mapper;

import es.ujaen.ahg00048.microservice_user.entity.User;
import es.ujaen.ahg00048.microservice_user.rest.DTO.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserMapper {
    @Autowired
    private PasswordEncoder _passwordEncoder;

    // User
    public User entity(UserDTO user) {
        return new User(user.email(), user.name(), "");
    }

    public User newEntity(UserDTO user) {
        return new User(user.email(), user.name(), _passwordEncoder.encode(user.password()));
    }

    public UserDTO dto(User user) {
        return new UserDTO(user.getEmail(), user.getName(), "");
    }
}
