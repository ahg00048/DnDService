package es.ujaen.ahg00048.api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.Duration;

import static org.springframework.cloud.gateway.server.mvc.filter.Bucket4jFilterFunctions.rateLimit;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class RouteConfiguration {
    @Value("${rateLimit.bucket.capacity}")
    private static int _rateLimitCapacity;
    @Value("${rateLimit.bucket.periodInMinutes}")
    private static int _rateLimitPeriodInMinutes;

//    @Bean
//    public RouterFunction<ServerResponse> getRouter() {
//        return route()
//                .GET("/api/users/**", http())
//                .filter(rateLimit(c -> c
//                        .setCapacity(_rateLimitCapacity)
//                        .setPeriod(Duration.ofMinutes(_rateLimitPeriodInMinutes))
//                        .setKeyResolver(request -> request.servletRequest().getRemoteAddr())))
//                .build();
//    }
}
