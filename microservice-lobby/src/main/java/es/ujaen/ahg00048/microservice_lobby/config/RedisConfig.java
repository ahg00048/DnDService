package es.ujaen.ahg00048.microservice_lobby.config;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Lobby> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Lobby> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setEnableTransactionSupport(true);

        return template;
    }
}
