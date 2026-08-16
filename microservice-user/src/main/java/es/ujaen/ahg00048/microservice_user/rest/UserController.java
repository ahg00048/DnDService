package es.ujaen.ahg00048.microservice_user.rest;

import es.ujaen.ahg00048.microservice_user.rest.DTO.UserDTO;
import es.ujaen.ahg00048.microservice_user.service.UserService;
import jakarta.websocket.server.PathParam;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@NoArgsConstructor
@RestController
public class UserController {
    @Autowired
    private UserService _service;


    @PostMapping("users")
    public void addUser() {
    }

    @GetMapping("users")
    public void getUsers() {
    }

    @GetMapping("users/{id}")
    public void getUser(@PathVariable String id) {

    }

    @DeleteMapping("users/{id}")
    public void removeUser(@PathVariable String id) {

    }

    @PutMapping("users/{id}")
    public void modifyUser(@PathVariable String id) {

    }
}
