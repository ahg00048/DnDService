package es.ujaen.ahg00048.microservice_lobby.config;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Duration;


@Configuration
@EnableTransactionManagement
public class TransactionalityConfig {
    private static final String LOCK_REGISTRY_REDIS_KEY = "locks";

    private static final Duration RELEASE_TIME_DURATION = Duration.ofSeconds(10);


    @Bean
    public RedisLockRegistry lockRegistry(RedisConnectionFactory redisConnectionFactory) {
        RedisLockRegistry redisLockRegistry = new RedisLockRegistry(
                redisConnectionFactory,
                LOCK_REGISTRY_REDIS_KEY,
                RELEASE_TIME_DURATION.toMillis());
        redisLockRegistry.setRedisLockType(RedisLockRegistry.RedisLockType.PUB_SUB_LOCK);

        return redisLockRegistry;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public RedisTemplate<String, Lobby> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Lobby> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setEnableTransactionSupport(true);

        return template;
    }

    @Bean
    MongoTemplate mongoTemplate(MongoDatabaseFactory dbFactory) {
        return new MongoTemplate(dbFactory);
    }
}
