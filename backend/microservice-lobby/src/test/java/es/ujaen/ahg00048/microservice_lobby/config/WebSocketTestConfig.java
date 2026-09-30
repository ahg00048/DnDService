package es.ujaen.ahg00048.microservice_lobby.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.List;


@Configuration
@Profile("test")
public class WebSocketTestConfig {
    @Bean
    public WebSocketStompClient webSocketStompClient() {
        WebSocketStompClient client = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        client.setMessageConverter(new JacksonJsonMessageConverter());
        return client;
    }
}
