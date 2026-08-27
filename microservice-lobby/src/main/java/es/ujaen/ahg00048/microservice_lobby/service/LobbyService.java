package es.ujaen.ahg00048.microservice_lobby.service;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Piece;
import es.ujaen.ahg00048.microservice_lobby.exception.LobbyRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.exception.UserRegistrationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Validated
public class LobbyService {
    private final Map<String, Lobby> _lobbiesRep = new HashMap<>();

    private final static int MAX_SAVED_BOARDS_PER_USER = 3;


    public Lobby createLobby(@Email String userId, boolean open, String password) throws LobbyRegistrationException {
        if (_lobbiesRep.values().stream().anyMatch((l -> l.getUsersIds().contains(userId))))
            throw new LobbyRegistrationException();

        Lobby lobby = new Lobby(userId, open, password);

        _lobbiesRep.put(lobby.getId(), lobby);

        return lobby;
    }

    public Lobby joinLobby(@Email String userId, String id) throws LobbyRegistrationException, UserRegistrationException {
        if (!_lobbiesRep.containsKey(id))
            throw new LobbyRegistrationException();

        Lobby lobby = _lobbiesRep.get(id);
        lobby.addUser(userId);

        return lobby;
    }

    public void leaveLobby(@Email String userId, String id) throws LobbyRegistrationException, UserRegistrationException {
        if (!_lobbiesRep.containsKey(id))
            throw new LobbyRegistrationException();

        Lobby lobby = _lobbiesRep.get(id);
        lobby.removeUser(userId);

        if (lobby.isEmpty())
            _lobbiesRep.remove(lobby.getId());
    }


    public Lobby updateLobbyBoard(@Email String userId, String id, @Valid Board board) throws LobbyRegistrationException, UserRegistrationException {
        if (!_lobbiesRep.containsKey(id))
            throw new LobbyRegistrationException();

        Lobby lobby = _lobbiesRep.get(id);
        lobby.updateBoard(userId, board);

        return lobby;
    }

    public Lobby addPiece(@Email String userId, String id, @Valid Board board) {
        if (!_lobbiesRep.containsKey(id))
            throw new LobbyRegistrationException();

        Lobby lobby = _lobbiesRep.get(id);

        return null;
    }

    public Lobby removePiece(@Email String userId, String id, @Valid Board board, Piece piece) {
        if (!_lobbiesRep.containsKey(id))
            throw new LobbyRegistrationException();

        Lobby lobby = _lobbiesRep.get(id);

        return null;
    }


    public void saveBoard(@Email String userId, Board board) {

    }

    public void removeBoard(@Valid String userId, Board board) {

    }
}
