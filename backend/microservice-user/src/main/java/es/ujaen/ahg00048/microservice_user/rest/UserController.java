package es.ujaen.ahg00048.microservice_user.rest;

import es.ujaen.ahg00048.microservice_user.qualifier.Password;
import es.ujaen.ahg00048.microservice_user.entity.User;
import es.ujaen.ahg00048.microservice_user.exception.UserAuthorizationException;
import es.ujaen.ahg00048.microservice_user.exception.UserRegistrationException;
import es.ujaen.ahg00048.microservice_user.rest.DTO.UserDTO;
import es.ujaen.ahg00048.microservice_user.rest.mapper.UserMapper;
import es.ujaen.ahg00048.microservice_user.service.UserService;
import jakarta.validation.ConstraintViolationException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@NoArgsConstructor
@Slf4j          // logging later
@Validated
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService _service;

    @Autowired
    private UserMapper _mapper;

    @Autowired
    private PasswordEncoder _pwdEncoder;

    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public void validationConstraintViolationException() {}

    @PostMapping
    public ResponseEntity<Void> addUser(@RequestBody UserDTO userD) {
        try {
           _service.addUser(_mapper.newEntity(userD));
           return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(@RequestParam(required = true, value = "id") String id) {
        try {
            User user = _service.getUser(id);
            List<User> users = _service.getUsers(user);
            return ResponseEntity.ok(users.stream().map(u -> _mapper.dto(u)).toList());
        } catch (UserAuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id,
                                           @RequestParam(required = true, value = "password") @Password String password) {
        try {
            User user = _service.getUser(id);

            if (!_pwdEncoder.matches(password, user.getPassword()))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            return ResponseEntity.ok(_mapper.dto(user));
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeUser(@PathVariable String id,
                                           @RequestParam(required = true, value = "userToRemove") String idToRemove) {
        try {
            User user = _service.getUser(id);
            _service.removeUser(user, idToRemove);
            return ResponseEntity.ok().build();
        } catch (UserAuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyUser(@PathVariable String id,
                                           @RequestParam(required = true, value = "newPassword") @Password String newPassword) {
        try {
            User user = _service.getUser(id);
            _service.changePassword(user, _pwdEncoder.encode(newPassword));
            return ResponseEntity.ok().build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
