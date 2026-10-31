package es.ujaen.ahg00048.microservice_lobby.config;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.port.ws}")
    private int port;

    @Value("${rabbitmq.virtual-host.ws}")
    private String vHost;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    private static final int MAX_MESSAGE_SIZE = 8192;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websock")
                .setAllowedOriginPatterns("*")
        // .withSockJS()
        // .setHeartbeatTime(30000)
        ;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/publish");
        config.enableStompBrokerRelay("/topic")
                .setRelayHost(host)
                .setRelayPort(port)
                .setVirtualHost(vHost)
                .setClientLogin(username)
                .setClientPasscode(password);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(MAX_MESSAGE_SIZE);
        registry.setSendBufferSizeLimit(MAX_MESSAGE_SIZE);

        registry.setTimeToFirstMessage(2000);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NonNull Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                accessor.setLeaveMutable(true);
                StompCommand command = accessor.getCommand();

                if (StompCommand.SEND.equals(command)) {
                    String topicExchangePrefix = "topic";
                    String destination = accessor.getDestination();

                    if (destination != null && destination.contains(topicExchangePrefix)) {
                        return null;
                    }
                }

                return message;
            }
        });
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(MAX_MESSAGE_SIZE);
        container.setMaxBinaryMessageBufferSize(MAX_MESSAGE_SIZE);
        return container;
    }
}
