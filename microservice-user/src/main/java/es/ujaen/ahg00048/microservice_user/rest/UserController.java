package es.ujaen.ahg00048.microservice_user.rest;

import es.ujaen.ahg00048.microservice_user.entity.User;
import es.ujaen.ahg00048.microservice_user.exception.UserAuthenticationException;
import es.ujaen.ahg00048.microservice_user.exception.UserAuthorizationException;
import es.ujaen.ahg00048.microservice_user.exception.UserRegistrationException;
import es.ujaen.ahg00048.microservice_user.rest.DTO.JwtResponseDTO;
import es.ujaen.ahg00048.microservice_user.rest.DTO.UserDTO;
import es.ujaen.ahg00048.microservice_user.rest.mapper.UserMapper;
import es.ujaen.ahg00048.microservice_user.security.CredentialsService;
import es.ujaen.ahg00048.microservice_user.security.jwt.JwtService;
import es.ujaen.ahg00048.microservice_user.service.UserService;
import jakarta.validation.ConstraintViolationException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@NoArgsConstructor
@Slf4j          // logging later
@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private UserService _service;

    @Autowired
    private UserMapper _mapper;

    @Autowired
    private AuthenticationManager _authManager;

    @Autowired
    private JwtService _jwtService;



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
    public ResponseEntity<JwtResponseDTO> getUser(@PathVariable String id, @RequestParam(required = true, value = "password") String password) {
        try {
            User user = _service.getUser(id);

            Authentication auth = _authManager.authenticate(new UsernamePasswordAuthenticationToken(id, password));
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = _jwtService.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponseDTO(token, _mapper.dto(user)));
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeUser(@PathVariable String id, @RequestParam(required = true, value = "idToRemove") String idToRemove) {
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
    public ResponseEntity<Void> modifyUser(@PathVariable String id, @RequestParam(required = true, value = "newPassword") String newPassword) { /// change in the future
        try {
            User user = _service.getUser(id);
            _service.changePassword(user, newPassword);
            return ResponseEntity.ok().build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
