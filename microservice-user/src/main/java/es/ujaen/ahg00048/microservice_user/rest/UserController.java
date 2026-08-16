package es.ujaen.ahg00048.microservice_user.rest;

import es.ujaen.ahg00048.microservice_user.rest.DTO.UserDTO;
import es.ujaen.ahg00048.microservice_user.service.UserService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@NoArgsConstructor
@RestController
public class UserController {
    @Autowired
    private UserService _service;

    @GetMapping("users/{id}")
    public void getUser() {

    }

    @GetMapping("users")
    public void getUsers() {

    }

    @DeleteMapping("users/{id}")
    public void removeUser() {

    }

    @PostMapping("users/{id}")
    public void addUser() {

    }

    @PutMapping("users/{id}")
    public void modifyUser() {

    }
}
