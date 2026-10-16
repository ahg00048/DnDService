package es.ujaen.ahg00048.microservice_user.rest;

import es.ujaen.ahg00048.microservice_user.exception.UserAuthorizationException;
import es.ujaen.ahg00048.microservice_user.exception.UserRegistrationException;
import es.ujaen.ahg00048.microservice_user.rest.DTO.UserDTO;
import es.ujaen.ahg00048.microservice_user.rest.mapper.UserMapper;
import es.ujaen.ahg00048.microservice_user.service.JwtService;
import es.ujaen.ahg00048.microservice_user.service.UserService;
import jakarta.validation.ConstraintViolationException;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService _userService;

    @Autowired
    private JwtService _jwtService;

    @Autowired
    private UserMapper _mapper;


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public void validationConstraintViolationException() {}

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsers(@AuthenticationPrincipal Jwt auth) {
        try {
            List<UserRepresentation> users = _userService.getUsers();
            return ResponseEntity.ok(users.stream().map(u -> _mapper.dto(u)).toList());
        } catch (UserAuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeUser(@AuthenticationPrincipal Jwt auth,
                                           @PathVariable String id) {
        try {
            _userService.removeUser(auth.getSubject(), _jwtService.hasRole(auth, "ADMIN"), id);
            return ResponseEntity.ok().build();
        } catch (UserAuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}