package es.ujaen.ahg00048.microservice_image.config;


import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;

import java.time.Duration;


@Configuration
public class RabbitMQConfig {
    @Bean
    public ConnectionFactory connectionFactory(@Value("${spring.rabbitmq.host}") String host,
                                               @Value("${spring.rabbitmq.port}") int port,
                                               @Value("${spring.rabbitmq.virtual-host}") String vHost,
                                               @Value("${spring.rabbitmq.username}") String username,
                                               @Value("${spring.rabbitmq.password}") String password) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setVirtualHost(vHost);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         @Value("${rabbitmq.exchange.image}") String exchange,
                                         @Value("${rabbitmq.bind.key.image}") String routingKey) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        RetryPolicy retryPolicy = RetryPolicy.builder()
                .delay(Duration.ofMillis(500))
                .multiplier(2.0)
                .maxDelay(Duration.ofSeconds(10))
                .build();
        template.setMessageConverter(messageConverter());
        template.setRetryTemplate(new RetryTemplate(retryPolicy));

        template.setExchange(exchange);
        template.setRoutingKey(routingKey);

        return template;
    }

    @Bean
    public SimpleMessageConverter messageConverter() {
        return new SimpleMessageConverter();
    }
}