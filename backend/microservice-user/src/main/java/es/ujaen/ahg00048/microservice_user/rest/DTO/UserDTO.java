package es.ujaen.ahg00048.microservice_user.rest.DTO;


public record UserDTO(String id, String name) {
    public UserDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

