package es.ujaen.ahg00048.microservice_user.rest;

import es.ujaen.ahg00048.microservice_user.entity.User;
import es.ujaen.ahg00048.microservice_user.exception.UserAuthenticationException;
import es.ujaen.ahg00048.microservice_user.exception.UserAuthorizationException;
import es.ujaen.ahg00048.microservice_user.exception.UserRegistrationException;
import es.ujaen.ahg00048.microservice_user.rest.DTO.UserDTO;
import es.ujaen.ahg00048.microservice_user.rest.mapper.UserMapper;
import es.ujaen.ahg00048.microservice_user.service.UserService;
import jakarta.websocket.server.PathParam;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor
@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private UserService _service;

    @Autowired
    private UserMapper _mapper;

    @PostMapping
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userD) {
        try {
           _service.addUser(_mapper.newEntity(userD));
           return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(@RequestParam(required = true, value = "id") String email) {
        try {
            User user = _service.getUser(email);
            List<User> users = _service.getUsers(user);
            return ResponseEntity.ok(users.stream().map(u -> _mapper.dto(u)).toList());
        } catch (UserAuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id, @RequestParam(required = true, value = "password") String password) {
        try {
            User user = _service.login(id, password);
            return ResponseEntity.ok(_mapper.dto(user));
        } catch (UserAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeUser(@PathVariable String id, @RequestParam(required = true, value = "userToRemove") String email) {
        try {
            User user = _service.getUser(id);
            _service.removeUser(user, email);
            return ResponseEntity.ok().build();
        } catch (UserAuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ????????????????????????????????????????????????? NOT YET
    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyUser(@PathVariable String id, @RequestParam(required = true, value = "newPassword") String newPassword) {
        try {
            User user = _service.getUser(id);

            return ResponseEntity.ok().build();
        } catch (UserAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
