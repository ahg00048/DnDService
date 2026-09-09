package es.ujaen.ahg00048.microservice_lobby.service;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Piece;
import es.ujaen.ahg00048.microservice_lobby.exception.BoardRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.exception.InvalidOperationException;
import es.ujaen.ahg00048.microservice_lobby.exception.LobbyRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.exception.UserRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.repository.mongo.BoardRepository;
import es.ujaen.ahg00048.microservice_lobby.repository.redis.LobbyRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Validated
public class LobbyService {
    @Autowired
    private LobbyRepository _lobbiesRep;

    @Autowired
    private BoardRepository _boardsRep;


    public static int MAX_BOARDS_PER_USER;
    public static int MAX_USERS_PER_LOBBY;
    public static int MAX_PIECES_PER_BOARDS;


    @Autowired
    public LobbyService(
            @Value("${app.user.max.boards}") int max_boards_per_user,
            @Value("${app.lobby.max.users}") int max_users_per_lobby,
            @Value("${app.lobby.board.max.pieces}") int max_pieces_per_boards) {
        MAX_BOARDS_PER_USER = max_boards_per_user;
        MAX_USERS_PER_LOBBY = max_users_per_lobby;
        MAX_PIECES_PER_BOARDS = max_pieces_per_boards;
    }


    /// Lobbies logic -----------------------------------------------------------------------------------------------------------

    public List<Lobby> getPublicLobbies() {
        return _lobbiesRep.findAllOpen();
    }

    public Lobby createLobby(@Email @NotBlank String userId, boolean open, String password) throws LobbyRegistrationException {
        if (_lobbiesRep.existByUserIdsContaining(userId))
            throw new LobbyRegistrationException();

        Lobby lobby = new Lobby(userId, open, password);

        return _lobbiesRep.insert(lobby);
    }

    public Lobby joinLobby(@Email @NotBlank String userId, String id, String password) throws LobbyRegistrationException, UserRegistrationException, InvalidOperationException {
        Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

        lobby.addUser(userId, password);

        return _lobbiesRep.save(lobby);
    }

    public Optional<Lobby> leaveLobby(@Email @NotBlank String userId, String id) throws LobbyRegistrationException, UserRegistrationException {
        Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

        lobby.removeUser(userId);

        if (lobby.isEmpty()) {
            _lobbiesRep.deleteById(lobby.getId());
            return Optional.empty();
        }

        return Optional.of(_lobbiesRep.save(lobby));
    }

    /// Boards game logic -----------------------------------------------------------------------------------------------------------

    public Lobby addPiece(@Email @NotBlank String userId, String id) throws LobbyRegistrationException, UserRegistrationException, InvalidOperationException {
        Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

        if (!lobby.contains(userId))
            throw new UserRegistrationException();

        lobby.getBoard().addPiece();

        return _lobbiesRep.save(lobby);
    }

    public Lobby updatePiece(@Email @NotBlank String userId, String id, @Valid Piece piece) throws LobbyRegistrationException, UserRegistrationException, InvalidOperationException {
        Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

        if (!lobby.contains(userId))
            throw new UserRegistrationException();

        lobby.getBoard().updatePiece(piece);

        return _lobbiesRep.save(lobby);
    }

    public Lobby removePiece(@Email @NotBlank String userId, String id, @Valid Piece piece) throws LobbyRegistrationException, UserRegistrationException, InvalidOperationException {
        Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

        if (!lobby.contains(userId))
            throw new UserRegistrationException();

        lobby.getBoard().removePiece(piece);

        return _lobbiesRep.save(lobby);
    }

    public Lobby modifyBoardProperties(@Email @NotBlank String userId, String id, String imageId, int scale) throws LobbyRegistrationException, UserRegistrationException {
        Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

        if (!lobby.contains(userId))
            throw new UserRegistrationException();

        lobby.getBoard().setBackgroundImage(imageId);
        lobby.getBoard().setScale(scale);

        return _lobbiesRep.save(lobby);
    }

    public Lobby clearBoard(@Email @NotBlank String userId, String id) throws LobbyRegistrationException, UserRegistrationException {
        Lobby lobby = _lobbiesRep.findById(id).orElseThrow(LobbyRegistrationException::new);

        if (!lobby.contains(userId))
            throw new UserRegistrationException();

        lobby.getBoard().clear();

        return _lobbiesRep.save(lobby);
    }

    /// Boards persistence logic -----------------------------------------------------------------------------------------------------------

    public Board addBoard(@Email @NotBlank String userId, @Valid Board board) throws BoardRegistrationException {
        if (_boardsRep.findByUserId(userId).size() >= MAX_BOARDS_PER_USER)
            throw new BoardRegistrationException();

        board.initId();
        board.setUserId(userId);

        return _boardsRep.insert(board);
    }

    public Board saveBoard(@Email @NotBlank String userId, @Valid Board board) throws BoardRegistrationException, InvalidOperationException {
        Board savedBoard = _boardsRep.findById(board.getId()).orElseThrow(BoardRegistrationException::new);

        if (!savedBoard.getUserId().equals(userId))
            throw new InvalidOperationException();

        return _boardsRep.save(board);
    }

    public List<Board> getSavedBoards(@Email @NotBlank String userId) {
        return _boardsRep.findByUserId(userId);
    }

    public void removeBoard(@Email @NotBlank String userId, @Valid Board board) throws BoardRegistrationException, InvalidOperationException {
        Board savedBoard = _boardsRep.findById(board.getId()).orElseThrow(BoardRegistrationException::new);

        if (!savedBoard.getUserId().equals(userId))
            throw new InvalidOperationException();

        _boardsRep.delete(savedBoard);
    }
}
