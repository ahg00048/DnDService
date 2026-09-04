package es.ujaen.ahg00048.microservice_lobby.entity;

import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import es.ujaen.ahg00048.microservice_lobby.exception.UserRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.service.LobbyService;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Lobby {
    private String id;
    private boolean open;       // public/private lobby
    private String password;

    private final List<String> usersIds;
    private Board board;


    public Lobby(String lobbyCreator, boolean open, String password) {
        id = new ObjectId().toString();

        usersIds = new ArrayList<>();
        usersIds.add(lobbyCreator);

        this.open = open;
        this.password = password;
    }


    public void addUser(String userId) {
        if (usersIds.contains(userId) || usersIds.size() >= LobbyService.MAX_USERS_PER_LOBBY)
            throw new UserRegistrationException();

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
