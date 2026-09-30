package es.ujaen.ahg00048.microservice_user.rest.DTO;


public record UserDTO(String email, String name, String password) {
    public UserDTO(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
}

