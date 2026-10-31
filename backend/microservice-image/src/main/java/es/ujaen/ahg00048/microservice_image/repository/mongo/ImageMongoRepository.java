package es.ujaen.ahg00048.microservice_image.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import es.ujaen.ahg00048.microservice_image.entity.Image;

@Transactional
public interface ImageMongoRepository extends MongoRepository<Image, String> {
    List<Image> findAllByUserId(String userId);
}
