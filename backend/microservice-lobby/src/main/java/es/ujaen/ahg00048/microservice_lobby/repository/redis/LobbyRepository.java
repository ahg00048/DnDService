package es.ujaen.ahg00048.microservice_lobby.repository.redis;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
public class LobbyRepository {
    @Autowired
    private RedisTemplate<String, Lobby> _template;

    private final static String _redisHashKey = "lobbies";


    public Optional<Lobby> findById(String id) {
        return Optional.ofNullable((Lobby) _template.opsForHash().get(_redisHashKey, id));
    }

    public List<Lobby> findAll() {
        return _template.opsForHash().values(_redisHashKey).stream().map(o -> (Lobby) o).toList();
    }

    public List<Lobby> findAllOpen() {
        return _template.opsForHash().values(_redisHashKey).stream().map(o -> (Lobby) o).filter(Lobby::isOpen).toList();
    }

    public List<Lobby> findAllByImageId(String imageId) {
        return _template.opsForHash().values(_redisHashKey)
                .stream()
                .map(o -> (Lobby) o)
                .filter(l -> {
            Board board = l.getBoard();
            return !(board.getBackgroundImage().equals(imageId) ||
                    board.getPieces().stream().anyMatch(p -> p.getImageId().equals(imageId)));
        }).toList();
    }

    public Optional<Lobby> findByUserId(String userId) {
        return _template.opsForHash().values(_redisHashKey)
                .stream()
                .map(o -> (Lobby) o).filter(l -> l.contains(userId)).findFirst();
    }

    public boolean existByUserIdsContaining(String userId) {
        return _template.opsForHash().values(_redisHashKey)
                .stream()
                .map(o -> (Lobby) o)
                .anyMatch(l -> l.contains(userId));
    }

    public Lobby insert(Lobby lobby) {
        _template.opsForHash().put(_redisHashKey, lobby.getId(), lobby);

        return (Lobby) _template.opsForHash().get(_redisHashKey, lobby.getId());
    }

    public Lobby save(Lobby lobby) {
        _template.opsForHash().put(_redisHashKey, lobby.getId(), lobby);

        return (Lobby) _template.opsForHash().get(_redisHashKey, lobby.getId());
    }

    public void saveAll(List<Lobby> lobbies) {
        _template.opsForHash().putAll(_redisHashKey, lobbies
                .stream()
                .collect(Collectors.toMap(Lobby::getId, l -> l)));
    }

    public void deleteById(String id) {
        _template.opsForHash().delete(_redisHashKey, id);
    }

    public void delete(Lobby lobby) {
        _template.opsForHash().delete(_redisHashKey, lobby.getId());
    }
}