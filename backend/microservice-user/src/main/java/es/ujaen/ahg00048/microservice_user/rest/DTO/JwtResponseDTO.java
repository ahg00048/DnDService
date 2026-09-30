package es.ujaen.ahg00048.microservice_user.rest.DTO;

public record JwtResponseDTO(String access_token, UserDTO user) {
    public JwtResponseDTO(String access_token, UserDTO user) {
        this.access_token = access_token;
        this.user = user;
    }
}
