package es.ujaen.ahg00048.microservice_user.utils;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;


public class JwtUtils {
    public static Boolean hasRole(Jwt jwt, String auth) {
        List<String> auths = jwt.getClaimAsStringList("authorities");

        return auths.stream().anyMatch(r -> r.equals(auth));
    }
}
