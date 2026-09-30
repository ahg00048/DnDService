package es.ujaen.ahg00048.microservice_image.repository.mongo;

import es.ujaen.ahg00048.microservice_image.entity.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
public interface ImageMongoRepository extends MongoRepository<Image, String> {
    List<Image> findAllByUserId(String userId);
}
