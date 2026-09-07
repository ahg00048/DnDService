package es.ujaen.ahg00048.microservice_lobby.entity;

import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import es.ujaen.ahg00048.microservice_lobby.exception.InvalidOperationException;
import es.ujaen.ahg00048.microservice_lobby.exception.UserRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.service.LobbyService;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RedisHash("lobbies")
public class Lobby {
    @Id
    private String id;
    private boolean open;       // public/private lobby
    private String password;

    private final List<String> usersIds;
    private Board board;


    public Lobby(String lobbyCreator, boolean open, String password) {
        id = new ObjectId().toString();

        board = new Board();
        usersIds = new ArrayList<>();
        usersIds.add(lobbyCreator);

        this.open = open;
        this.password = password;
    }


    public void addUser(String userId, String password) {
        if (usersIds.contains(userId) || usersIds.size() >= LobbyService.MAX_USERS_PER_LOBBY)
            throw new UserRegistrationException();
        if (!password.equals(this.password) && !open)
            throw new InvalidOperationException();

        usersIds.add(userId);
    }

    public void removeUser(String userId) {
        if (!usersIds.contains(userId))
            throw new UserRegistrationException();

        usersIds.remove(userId);
    }

    public boolean isEmpty() {
        return usersIds.isEmpty();
    }

    public boolean contains(String userId) {
        return usersIds.contains(userId);
    }
}
