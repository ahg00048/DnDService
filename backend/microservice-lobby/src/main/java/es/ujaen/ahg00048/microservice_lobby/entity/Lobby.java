package es.ujaen.ahg00048.microservice_lobby.entity;

import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import es.ujaen.ahg00048.microservice_lobby.exception.InvalidOperationException;
import es.ujaen.ahg00048.microservice_lobby.exception.UserRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.service.LobbyService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class Lobby implements Serializable {
    private String id;
    private boolean open;       // public/private lobby
    private String password;

    private Board board;
    private List<PlayerInfo> playersInfo;


    public Lobby(String lobbyCreatorId, String lobbyCreatorName, boolean open, String password) {
        id = new ObjectId().toString();

        board = new Board();
        playersInfo = new ArrayList<>();
        playersInfo.add(new PlayerInfo(lobbyCreatorId, lobbyCreatorName));

        this.open = open;
        this.password = password;
    }


    public void addUser(String userId, String userName, String password)
            throws UserRegistrationException, InvalidOperationException {
        if (playersInfo.contains(userId) || playersInfo.size() >= LobbyService.MAX_USERS_PER_LOBBY)
            throw new UserRegistrationException();
        if (!password.equals(this.password) && !open)
            throw new InvalidOperationException();

        playersInfo.add(new PlayerInfo(userId, userName));
    }

    public void removeUser(String userId)
            throws UserRegistrationException {
        PlayerInfo playerInfo = new PlayerInfo(userId);

        if (!playersInfo.contains(playerInfo))
            throw new UserRegistrationException();

        playersInfo.remove(playerInfo);
    }

    public boolean isEmpty() {
        return playersInfo.isEmpty();
    }

    public boolean contains(String userId) {
        return playersInfo.contains(new PlayerInfo(userId));
    }
}
