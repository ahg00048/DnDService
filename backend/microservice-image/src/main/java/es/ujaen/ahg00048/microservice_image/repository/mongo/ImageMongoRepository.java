package es.ujaen.ahg00048.microservice_image.repository.mongo;

import es.ujaen.ahg00048.microservice_image.entity.image.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface ImageMongoRepository extends MongoRepository<Image, String> {
    List<Image> findAllByUserId(String userId);
}
