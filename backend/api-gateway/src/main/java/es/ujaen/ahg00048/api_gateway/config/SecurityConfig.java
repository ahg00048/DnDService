package es.ujaen.ahg00048.api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.OidcBackChannelServerLogoutHandler;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.server.session.InMemoryReactiveOidcSessionRegistry;
import org.springframework.security.oauth2.client.oidc.server.session.ReactiveOidcSessionRegistry;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    private ReactiveClientRegistrationRepository _clientRegistrationRepository;


//    @Bean
//    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        config.addAllowedOrigin("*");
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return source;
//    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
//                .csrf(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(a -> a.anyExchange().authenticated())
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler(_clientRegistrationRepository)))
                .oidcLogout(logout -> logout
                        .backChannel(Customizer.withDefaults()))
                .build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRep) {
        OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRep);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }

    @Bean
    public ReactiveOidcSessionRegistry reactiveOidcSessionRegistry() {
        return new InMemoryReactiveOidcSessionRegistry();
    }

    @Bean
    OidcBackChannelServerLogoutHandler oidcLogoutHandler(ReactiveOidcSessionRegistry sessionRegistry) {
        OidcBackChannelServerLogoutHandler logoutHandler = new OidcBackChannelServerLogoutHandler(sessionRegistry);
        logoutHandler.setSessionCookieName("SESSION");
        return logoutHandler;
    }
}
