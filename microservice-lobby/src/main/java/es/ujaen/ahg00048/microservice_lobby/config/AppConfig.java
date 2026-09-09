package es.ujaen.ahg00048.microservice_lobby.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;


@Configuration
public class AppConfig {
    @Bean
    public RedisTemplate<String, Lobby> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Lobby> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.afterPropertiesSet();

        return template;
    }
}
