package es.ujaen.ahg00048.microservice_user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class JwtService {
    @Value("${keycloak.users.clientId}")
    private String _clientId;


    public Boolean hasRole(Jwt jwt, String auth) {
        List<String> auths = jwt.getClaimAsStringList("authorities");

        return auths.stream().anyMatch(r -> r.equals(auth));
    }
}
