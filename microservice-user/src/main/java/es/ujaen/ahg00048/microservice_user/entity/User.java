package es.ujaen.ahg00048.microservice_user.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Document("users")
public class User {
    @Id
    @Email
    private String email;
    @NotBlank
    @Size(min = 6, max = 16)
    private String name;
    @NotBlank
    @Size(min = 8)
    private String password;


    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }


    @Override
    public boolean equals(Object obj) {
        return email.equals(((User) obj).getEmail());
    }
}