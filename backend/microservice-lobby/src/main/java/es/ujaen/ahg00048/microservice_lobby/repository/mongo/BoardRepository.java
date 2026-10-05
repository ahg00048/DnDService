package es.ujaen.ahg00048.microservice_lobby.repository.mongo;

import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
public interface BoardRepository extends MongoRepository<Board, String> {
    List<Board> findByUserId(String userId);
    void deleteAllByUserId(String userId);

    @Query(value = "{ 'pieces.imageId' : ?0, 'backgroundImage' : ?0 }")
    List<Board> findAllContainingImageId(String imageId);
}
