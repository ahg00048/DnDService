package es.ujaen.ahg00048.microservice_user.rest.DTO;

public record UserProfileDTO(String id, String username, String email, String firstName, String lastName, boolean isAdmin) {
}
