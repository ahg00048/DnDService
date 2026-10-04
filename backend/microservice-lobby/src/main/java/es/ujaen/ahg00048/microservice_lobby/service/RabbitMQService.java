package es.ujaen.ahg00048.microservice_lobby.service;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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

    @Service
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rabbitmq.queue.lobby.image}", durable = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.image}", ignoreDeclarationExceptions = "true"),
            key = "${rabbitmq.bind.key.image}"))
    public class ImageDeletionListener {
        @RabbitHandler
        public void imageDeletionListener(String imageId) {
            _lobbyService.removeImage_admin(imageId);
        }

        @RabbitHandler
        @Transactional
        public void imagesDeletionListener(List<String> imagesIds) {
            for (String id : imagesIds) {
                _lobbyService.removeImage_admin(id);
            }
        }
    }
}
