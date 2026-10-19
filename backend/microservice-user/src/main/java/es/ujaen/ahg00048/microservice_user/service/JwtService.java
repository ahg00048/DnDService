package es.ujaen.ahg00048.microservice_user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class JwtService {
    @Value("${keycloak.users.clientId}")
    private String _clientId;


    public Boolean hasRole(Jwt jwt, String role) {
        Map<String, Object> map = ((Map<String, Object>) jwt.getClaimAsMap("resource_access").get(_clientId));
        List<String> roles = (List<String>) map.get("roles");

        return roles.stream().anyMatch(r -> r.equals(role));
    }
}
