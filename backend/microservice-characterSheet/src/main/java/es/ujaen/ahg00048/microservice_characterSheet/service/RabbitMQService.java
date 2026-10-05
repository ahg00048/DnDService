package es.ujaen.ahg00048.microservice_characterSheet.service;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class RabbitMQService {
    @Autowired
    private CharacterSheetService _characterSheetService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rabbitmq.queue.charSheet.user}", durable = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.user}", ignoreDeclarationExceptions = "true"),
            key = "${rabbitmq.bind.key.user}"))
    public void userDeletionListener(String userId) {
        _characterSheetService.removeCharSheet_admin(userId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rabbitmq.queue.charSheet.image}", durable = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.image}", ignoreDeclarationExceptions = "true"),
            key = "${rabbitmq.bind.key.image}"))
    public void imageDeletionListener(String imageId) {
        _characterSheetService.removeImage_admin(imageId);
    }
}
