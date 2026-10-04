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
            value = @Queue(value = "${rabbitmq.queue.charSheet}", durable = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.user}", ignoreDeclarationExceptions = "true"),
            key = "${rabbitmq.bind.key.user}"))
    public void userDeletionListener(String userId) {
        _characterSheetService.removeCharSheet_admin(userId);
    }

    @Service
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rabbitmq.queue.charSheet}", durable = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.image}", ignoreDeclarationExceptions = "true"),
            key = "${rabbitmq.bind.key.image}"))
    public class ImageDeletionListener {
        @RabbitHandler
        public void imageDeletionListener(String imageId) {
            _characterSheetService.removeImage_admin(imageId);
        }

        @RabbitHandler
        @Transactional
        public void imagesDeletionListener(List<String> imagesIds) {
            for (String id : imagesIds) {
                _characterSheetService.removeImage_admin(id);
            }
        }
    }
}
