package es.ujaen.ahg00048.microservice_lobby.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

import es.ujaen.ahg00048.microservice_lobby.exception.UserRegistrationException;


@Service
public class RabbitMQService {
    @Autowired
    private LobbyService _lobbyService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rabbitmq.queue.lobby.user}", durable = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.user}", ignoreDeclarationExceptions = "true"),
            key = "${rabbitmq.bind.key.user}"),
            ackMode = "MANUAL")
    public void userDeletionListener(String userId, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long tag)
            throws IOException {
        try {
            _lobbyService.removeUser_admin(userId);
            channel.basicAck(tag, false);
        } catch (UserRegistrationException e) {
            channel.basicReject(tag, false);
        } catch (RuntimeException e) {
            channel.basicReject(tag, true);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rabbitmq.queue.lobby.image}", durable = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.image}", ignoreDeclarationExceptions = "true"),
            key = "${rabbitmq.bind.key.image}"))
    public void imageDeletionListener(String imageId) {
        _lobbyService.removeImage_admin(imageId);
    }
}
