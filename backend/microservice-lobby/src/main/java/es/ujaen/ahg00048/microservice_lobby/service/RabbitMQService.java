package es.ujaen.ahg00048.microservice_lobby.service;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RabbitMQService {
    @Autowired
    private LobbyService _lobbyService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rabbitmq.queue.lobby.user}", durable = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.user}", ignoreDeclarationExceptions = "true"),
            key = "${rabbitmq.bind.key.user}"))
    public void userDeletionListener(String userId) {
        _lobbyService.removeUser_admin(userId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rabbitmq.queue.lobby.image}", durable = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.image}", ignoreDeclarationExceptions = "true"),
            key = "${rabbitmq.bind.key.image}"))
    public void imageDeletionListener(String imageId) {
        _lobbyService.removeImage_admin(imageId);
    }
}
