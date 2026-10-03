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
    private static final Logger log = LoggerFactory.getLogger(RouteConfiguration.class);
    @Value("${rateLimit.bucket.capacity}")
    private int rateLimitCapacity;

    @Value("${rateLimit.bucket.refillInSeconds}")
    private int rateLimitRefillInSeconds;


    @Bean
    public RouterFunction<ServerResponse> getUserMicroserviceRouter() {
        return route("microservice-user")
                .GET("/api/v1/users/**", http())
                .filter(
                    rateLimit(c -> c.setCapacity(rateLimitCapacity)
                        .setPeriod(Duration.ofSeconds(rateLimitRefillInSeconds))
                            .setHeaderName("tokens-left")
                        .setKeyResolver(request -> request.servletRequest().getRemoteAddr()))
                .andThen(
                    lb("MICROSERVICE-USER")))
                .build();
    }
}