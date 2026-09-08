package es.ujaen.ahg00048.microservice_lobby.repository.mongo;

import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BoardRepository extends MongoRepository<Board, String> {
    List<Board> findByUserId(String userId);
}
