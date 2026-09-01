package es.ujaen.ahg00048.microservice_lobby.service;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Piece;
import es.ujaen.ahg00048.microservice_lobby.exception.BoardRegistrationException;
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
    private final Map<String, Board> _boardsRep = new HashMap<>();

    private final static int MAX_BOARDS_PER_USER = 3;


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


    public Lobby addPiece(@Email String userId, String id) {
        if (!_lobbiesRep.containsKey(id))
            throw new LobbyRegistrationException();

        Lobby lobby = _lobbiesRep.get(id);
        if (!lobby.contains(userId))
            throw new UserRegistrationException();

        lobby.getBoard().addPiece();

        return lobby;
    }

    public Lobby updatePiece(@Email String userId, String id, @Valid Piece piece) {
        if (!_lobbiesRep.containsKey(id))
            throw new LobbyRegistrationException();

        Lobby lobby = _lobbiesRep.get(id);
        if (!lobby.contains(userId))
            throw new UserRegistrationException();

        lobby.getBoard().updatePiece(piece);

        return lobby;
    }

    public Lobby removePiece(@Email String userId, String id, @Valid Piece piece) {
        if (!_lobbiesRep.containsKey(id))
            throw new LobbyRegistrationException();

        Lobby lobby = _lobbiesRep.get(id);
        if (!lobby.contains(userId))
            throw new UserRegistrationException();

        lobby.getBoard().removePiece(piece);

        return lobby;
    }

    public Lobby modifyBoardProperties(@Email String userId, String id, String imageId, int scale) {
        if (!_lobbiesRep.containsKey(id))
            throw new LobbyRegistrationException();

        Lobby lobby = _lobbiesRep.get(id);
        if (!lobby.contains(userId))
            throw new UserRegistrationException();

        lobby.getBoard().setBackground(imageId);
        lobby.getBoard().setScale(scale);

        return lobby;
    }


    public void addBoard(String userId, @Valid Board board) {
        if (_boardsRep.values().stream().filter(b -> b.getUserId().equals(userId)).toList().size() >= MAX_BOARDS_PER_USER)
            throw new BoardRegistrationException();

        board.initId();
        board.setUserId(userId);

        _boardsRep.put(board.getId(), board);
    }

    public void saveBoard(String userId, @Valid Board board) {
        if (!_boardsRep.containsKey(board.getId()) || !_boardsRep.get(board.getId()).getUserId().equals(userId))
            throw new BoardRegistrationException();

        _boardsRep.put(board.getId(), board);
    }

    public List<Board> getSavedBoards(String userId) {
        return _boardsRep.values().stream().filter(b -> b.getUserId().equals(userId)).toList();
    }

    public void removeBoard(String userId, @Valid Board board) {
        if (!_boardsRep.containsKey(board.getId()) || !_boardsRep.get(board.getId()).getUserId().equals(userId))
            throw new BoardRegistrationException();

        _boardsRep.remove(board.getId());
    }
}
