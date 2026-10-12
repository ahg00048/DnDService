package es.ujaen.ahg00048.microservice_lobby.service;

import es.ujaen.ahg00048.microservice_lobby.exception.*;
import es.ujaen.ahg00048.microservice_lobby.repository.mongo.BoardRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Piece;

import java.util.List;


@SpringBootTest(classes = es.ujaen.ahg00048.microservice_lobby.app.MicroserviceLobbyApplication.class)
@ActiveProfiles("test")
public class LobbyServiceTest {
    @Autowired
    private LobbyService _lobbyService;

    @Autowired
    private MongoTemplate _mongoTemplate;

    @Autowired
    private RedisTemplate<String, Lobby> _redisTemplate;


    @PostConstruct
    @AfterEach
    public void cleanUp() {
        _mongoTemplate.getDb().drop();
        _redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
    }


    @Test
    @DirtiesContext
    public void joinAndLeaveLobby() {
        String validUserId1 = "random1-id";
        String user1Name = "user1";
        String validUserId2 = "random2-id";
        String user2Name = "user2";

        Assertions.assertThrows(ConstraintViolationException.class, () -> _lobbyService.createLobby("", "", false, "secret")); // Create lobby with invalid user id

        String password = "secret";
        Lobby lobby = _lobbyService.createLobby(validUserId1, user1Name, false, password); // Create lobby

        Assertions.assertThrows(LobbyRegistrationException.class, () -> _lobbyService.createLobby(validUserId1, user1Name, false, "secret")); // Trying to create a lobby while being in one

        Assertions.assertThrows(InvalidOperationException.class, () -> _lobbyService.joinLobby(validUserId2, user2Name, lobby.getId(), "")); // Join lobby with wrong password

        Assertions.assertDoesNotThrow(() -> _lobbyService.joinLobby(validUserId2, user2Name, lobby.getId(), password)); // Join lobby

        Assertions.assertThrows(UserRegistrationException.class, () -> _lobbyService.joinLobby(validUserId2, user2Name, lobby.getId(), password));  // Trying to join into a lobby already joined

        Assertions.assertDoesNotThrow(() -> _lobbyService.leaveLobby(validUserId2, lobby.getId())); // User 2 leaves Lobby

        Assertions.assertThrows(UserRegistrationException.class, () -> _lobbyService.leaveLobby(validUserId2, lobby.getId())); // User 2 tries to leave the lobby again

        Assertions.assertDoesNotThrow(() -> _lobbyService.leaveLobby(validUserId1, lobby.getId())); // User 2 leaves Lobby

        Assertions.assertThrows(LobbyRegistrationException.class, () -> _lobbyService.joinLobby(validUserId1, user1Name, lobby.getId(), password)); // User trying to join nonexisting lobby
    }

    @Test
    @DirtiesContext
    public void publicLobbies() {
        String validUserId1 = "random1-id";
        String user1Name = "user1";
        String validUserId2 = "random2-id";
        String user2Name = "user2";

        Lobby lobby = _lobbyService.createLobby(validUserId1, user1Name, false, "secret"); // Create closed lobby
        String lobbyId1 = lobby.getId();

        Assertions.assertEquals(0, _lobbyService.getPublicLobbies().size()); // Expect empty

        lobby = _lobbyService.createLobby(validUserId2, user2Name, true, "secret"); // Create closed lobby
        String lobbyId2 = lobby.getId();

        Assertions.assertEquals(1, _lobbyService.getPublicLobbies().size()); // Expect 1

        _lobbyService.leaveLobby(validUserId1, lobbyId1); // leave lobbies
        _lobbyService.leaveLobby(validUserId2, lobbyId2);

        Assertions.assertEquals(0, _lobbyService.getPublicLobbies().size()); // Expect empty
    }

    @Test
    @DirtiesContext
    public void boardGameOperations() {
        String validUserId1 = "random1-id";
        String user1Name = "user1";
        String validUserId2 = "random2-id";
        String user2Name = "user2";

        String password = "secret";
        Lobby lobby = _lobbyService.createLobby(validUserId1, user1Name, false, password);
        final String lobbyId = lobby.getId();

        Assertions.assertThrows(UserRegistrationException.class, () ->  _lobbyService.addPiece(validUserId2, lobbyId)); // User not in lobby cant modify board in any way

        Assertions.assertThrows(UserRegistrationException.class, () ->  _lobbyService.modifyBoardProperties(validUserId2, lobbyId, "", 20));

        lobby = _lobbyService.joinLobby(validUserId2, user2Name, lobby.getId(), password);

        Assertions.assertEquals(0, lobby.getBoard().getPieces().size()); // There are no pieces in the board

        for (int i = 0; i < LobbyService.MAX_PIECES_PER_BOARDS - 1; i++) {
            Assertions.assertDoesNotThrow(() -> _lobbyService.addPiece(validUserId2, lobbyId)); // Add pieces
        }

        lobby = _lobbyService.addPiece(validUserId2, lobbyId);

        Assertions.assertThrows(PieceRegistrationException.class, () -> _lobbyService.addPiece(validUserId2, lobbyId)); // User is unable to add more pieces than the max

        Assertions.assertNotEquals(0, lobby.getBoard().getPieces().size()); // There is one pieces in the board

        Piece piece = lobby.getBoard().getPieces().getFirst();
        int newHp = 50;

        lobby = _lobbyService.updatePieceProperties_hp_maxHp(validUserId1, lobbyId, piece.getId(), newHp, piece.getMaxHp()); // Update piece
        final Piece constPiece = lobby.getBoard().getPieces().getFirst();

        Assertions.assertEquals(newHp, constPiece.getHp());

        Assertions.assertThrows(InvalidOperationException.class, () -> _lobbyService.removePiece(validUserId1, lobbyId, constPiece.getId()));

        Assertions.assertDoesNotThrow(() -> _lobbyService.selectPiece(validUserId1, lobbyId, constPiece.getId()));

        Assertions.assertDoesNotThrow(() -> _lobbyService.removePiece(validUserId1, lobbyId, constPiece.getId()));

        Assertions.assertThrows(PieceRegistrationException.class, () -> _lobbyService.removePiece(validUserId1, lobbyId, piece.getId()));
    }

    @Test
    @DirtiesContext
    public void boardPersistenceOperations() {
        String validUserId1 = "random1-id";
        String user1Name = "user1";
        String validUserId2 = "random2-id";
        String user2Name = "user2";

        String password = "secret";
        Lobby lobby = _lobbyService.createLobby(validUserId1, user1Name, false, password);
        lobby = _lobbyService.joinLobby(validUserId2, user2Name, lobby.getId(), password);

        lobby = _lobbyService.addPiece(validUserId1, lobby.getId());

        Piece piece = lobby.getBoard().getPieces().getFirst();

        final Board board = lobby.getBoard();

        for (int i = 0; i < LobbyService.MAX_BOARDS_PER_USER; i++) {
            Assertions.assertDoesNotThrow(() -> _lobbyService.addBoard(validUserId1, board));
        }

        Assertions.assertThrows(BoardRegistrationException.class, () -> _lobbyService.addBoard(validUserId1, board)); // User tries to add more than the permitted amount

        List<Board> savedBoards = _lobbyService.getSavedBoards(validUserId1);

        Assertions.assertEquals(LobbyService.MAX_BOARDS_PER_USER, savedBoards.size());

        savedBoards.getLast().setId("otherId");

        Assertions.assertThrows(BoardRegistrationException.class, () -> _lobbyService.saveBoard(validUserId1, savedBoards.getLast().getId(), savedBoards.getLast())); // Save not persistent board

        Assertions.assertThrows(InvalidOperationException.class, () -> _lobbyService.saveBoard(validUserId2, savedBoards.getFirst().getId(), savedBoards.getLast())); // Save valid board from other user

        Assertions.assertDoesNotThrow(() -> _lobbyService.removeBoard(validUserId1, savedBoards.getFirst().getId())); // Remove board

        List<Board> savedBoards2 = _lobbyService.getSavedBoards(validUserId1);

        Assertions.assertNotEquals(LobbyService.MAX_BOARDS_PER_USER, savedBoards2.size()); // Check that it has been removed
    }

    @Test
    @DirtiesContext
    public void BoardOperationsConcurrencyTest()
    {
        String validUserId1 = "random1-id";
        String user1Name = "user1";
        String validUserId2 = "random2-id";
        String user2Name = "user2";

        String password = "secret";

        Lobby lobby = _lobbyService.createLobby(validUserId1, user1Name, true, password);
        lobby = _lobbyService.joinLobby(validUserId2, user2Name, lobby.getId(), password);

        final String lobbyId = lobby.getId();

        for (int i = 0; i < LobbyService.MAX_PIECES_PER_BOARDS; i++) {
            lobby = _lobbyService.addPiece(validUserId2, lobbyId);
        }

        Assertions.assertEquals(LobbyService.MAX_PIECES_PER_BOARDS, lobby.getBoard().getPieces().size());

        _lobbyService.clearBoard(validUserId2, lobbyId);

        Thread thread = new Thread(() -> {
            for (int i = 0; i < LobbyService.MAX_PIECES_PER_BOARDS / 2; i++) {
                _lobbyService.addPiece(validUserId1, lobbyId);
            }
        });
        thread.start();

        for (int i = 0; i < LobbyService.MAX_PIECES_PER_BOARDS / 2; i++) {
            _lobbyService.addPiece(validUserId2, lobbyId);
        }

        try {
            thread.join();
        } catch (InterruptedException e) {
        }

        lobby = _lobbyService.getPublicLobbies().getFirst();
        Assertions.assertEquals(LobbyService.MAX_PIECES_PER_BOARDS, lobby.getBoard().getPieces().size());
    }
}