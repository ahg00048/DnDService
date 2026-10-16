package es.ujaen.ahg00048.microservice_user.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KeycloakClientConfig {
    @Value("${keycloak.baseUrl}")
    private String _keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String _keycloakRealm;

    @Value("${keycloak.credentials.clientId}")
    private String _keycloakClientId;

    @Value("${keycloak.credentials.clientSecret}")
    private String _keycloakClientSecret;


    @Bean
    public Keycloak keycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(_keycloakBaseUrl)
                .realm(_keycloakRealm)
                .clientId(_keycloakClientId)
                .clientSecret(_keycloakClientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
