package es.ujaen.ahg00048.microservice_user.rest;

import es.ujaen.ahg00048.microservice_user.exception.UserAuthorizationException;
import es.ujaen.ahg00048.microservice_user.exception.UserRegistrationException;
import es.ujaen.ahg00048.microservice_user.rest.DTO.UserDTO;
import es.ujaen.ahg00048.microservice_user.rest.mapper.UserMapper;
import es.ujaen.ahg00048.microservice_user.service.UserService;
import jakarta.validation.ConstraintViolationException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public void validationConstraintViolationException() {}


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsers(@AuthenticationPrincipal Jwt auth) {
        try {
            List<UserRepresentation> users = _service.getUsers();
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

//            User user = _service.getUser(id);
//            _service.removeUser(user, idToRemove);
            return ResponseEntity.ok().build();
        } catch (UserAuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}