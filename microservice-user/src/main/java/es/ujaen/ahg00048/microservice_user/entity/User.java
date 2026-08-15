package es.ujaen.ahg00048.microservice_user.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class User {
    @Email
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    @Size(min = 8)
    private String password;

    private final List<String> images = new ArrayList<>(); // store image ids
    private final List<String> characterSheets = new ArrayList<>();  // store characterSheets ids


    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
}