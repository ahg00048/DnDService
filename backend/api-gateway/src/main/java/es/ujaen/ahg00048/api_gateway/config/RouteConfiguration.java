package es.ujaen.ahg00048.api_gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.Duration;

import static org.springframework.cloud.gateway.server.mvc.filter.Bucket4jFilterFunctions.rateLimit;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;


@Configuration
public class RouteConfiguration {
    @Value("${rateLimit.bucket.capacity}")
    private int rateLimitCapacity;

    @Value("${rateLimit.bucket.refillInSeconds}")
    private int rateLimitRefillInSeconds;


    @Bean
    public RouterFunction<ServerResponse> getUserMicroserviceRouter() {
        return route("microservice-user")
                .GET("/api/v1/users/**", http())
                .POST("/api/v1/users/**", http())
                .DELETE("/api/v1/users/**", http())
                .PUT("/api/v1/users/**", http())
                .filter(
                        rateLimit(c -> c.setCapacity(rateLimitCapacity)
                                .setPeriod(Duration.ofSeconds(rateLimitRefillInSeconds))
                                .setKeyResolver(request -> request.servletRequest().getRemoteAddr()))
                                .andThen(
                                        lb("MICROSERVICE-USER")))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> getImageMicroserviceRouter() {
        return route("microservice-image")
                .GET("/api/v1/images/**", http())
                .POST("/api/v1/images/**", http())
                .DELETE("/api/v1/images/**", http())
                .filter(
                        rateLimit(c -> c.setCapacity(rateLimitCapacity)
                                .setPeriod(Duration.ofSeconds(rateLimitRefillInSeconds))
                                .setKeyResolver(request -> request.servletRequest().getRemoteAddr()))
                                .andThen(
                                        lb("MICROSERVICE-IMAGE")))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> getCharacterSheetMicroserviceRouter() {
        return route("microservice-characterSheet")
                .GET("/api/v1/charSheets/**", http())
                .POST("/api/v1/charSheets/**", http())
                .DELETE("/api/v1/charSheets/**", http())
                .PUT("/api/v1/charSheets/**", http())
                .filter(
                        rateLimit(c -> c.setCapacity(rateLimitCapacity)
                                .setPeriod(Duration.ofSeconds(rateLimitRefillInSeconds))
                                .setKeyResolver(request -> request.servletRequest().getRemoteAddr()))
                                .andThen(
                                        lb("MICROSERVICE-CHARACTERSHEET")))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> getLobbiesMicroservice_Rest_Router() {
        return route("microservice-lobby-rs")
                .GET("/api/v1/lobbies/**", http())
                .POST("/api/v1/lobbies/**", http())
                .DELETE("/api/v1/lobbies/**", http())
                .PUT("/api/v1/lobbies/**", http())
                .filter(
                        rateLimit(c -> c.setCapacity(rateLimitCapacity)
                                .setPeriod(Duration.ofSeconds(rateLimitRefillInSeconds))
                                .setKeyResolver(request -> request.servletRequest().getRemoteAddr()))
                                .andThen(
                                        lb("MICROSERVICE-LOBBY")))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> getLobbiesMicroservice_SockJS_Router() {
        return route("microservice-lobby-sockJs")
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> getLobbiesMicroservice_Ws_Router() {
        return route("microservice-lobby-ws").filter(
                    lb("ws:MICROSERVICE-LOBBY"))
                .build();
    }
}