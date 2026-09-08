package es.ujaen.ahg00048.microservice_lobby.service;

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
import es.ujaen.ahg00048.microservice_lobby.exception.InvalidOperationException;
import es.ujaen.ahg00048.microservice_lobby.exception.LobbyRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.exception.UserRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Piece;
import es.ujaen.ahg00048.microservice_lobby.exception.BoardRegistrationException;

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
        String validUserId1 = "random1@gmail.com";
        String validUserId2 = "random2@gmail.com";

        Assertions.assertThrows(ConstraintViolationException.class, () -> _lobbyService.createLobby("aaa", false, "secret")); // Create lobby with invalid user id

        String password = "secret";
        Lobby lobby = _lobbyService.createLobby(validUserId1, false, password); // Create lobby

        Assertions.assertThrows(LobbyRegistrationException.class, () -> _lobbyService.createLobby(validUserId1, false, "secret")); // Trying to create a lobby while being in one

        Assertions.assertThrows(InvalidOperationException.class, () -> _lobbyService.joinLobby(validUserId2, lobby.getId(), "")); // Join lobby with wrong password

        Assertions.assertDoesNotThrow(() -> _lobbyService.joinLobby(validUserId2, lobby.getId(), password)); // Join lobby

        Assertions.assertThrows(UserRegistrationException.class, () -> _lobbyService.joinLobby(validUserId2, lobby.getId(), password));  // Trying to join into a lobby already joined

        Assertions.assertDoesNotThrow(() -> _lobbyService.leaveLobby(validUserId2, lobby.getId())); // User 2 leaves Lobby

        Assertions.assertThrows(UserRegistrationException.class, () -> _lobbyService.leaveLobby(validUserId2, lobby.getId())); // User 2 tries to leave the lobby again

        Assertions.assertDoesNotThrow(() -> _lobbyService.leaveLobby(validUserId1, lobby.getId())); // User 2 leaves Lobby

        Assertions.assertThrows(LobbyRegistrationException.class, () -> _lobbyService.joinLobby(validUserId1, lobby.getId(), password)); // User trying to join nonexisting lobby
    }

    @Test
    @DirtiesContext
    public void publicLobbies() {
        String validUserId1 = "random1@gmail.com";
        String validUserId2 = "random2@gmail.com";

        Lobby lobby = _lobbyService.createLobby(validUserId1, false, "secret"); // Create closed lobby
        String lobbyId1 = lobby.getId();

        Assertions.assertEquals(0, _lobbyService.getPublicLobbies().size()); // Expect empty

        lobby = _lobbyService.createLobby(validUserId2, true, "secret"); // Create closed lobby
        String lobbyId2 = lobby.getId();

        Assertions.assertEquals(1, _lobbyService.getPublicLobbies().size()); // Expect 1

        _lobbyService.leaveLobby(validUserId1, lobbyId1); // leave lobbies
        _lobbyService.leaveLobby(validUserId2, lobbyId2);

        Assertions.assertEquals(0, _lobbyService.getPublicLobbies().size()); // Expect empty
    }

    @Test
    @DirtiesContext
    public void boardGameOperations() {
        String validUser1Id = "random1@gmail.com";
        String validUser2Id = "random2@gmail.com";

        String password = "secret";
        Lobby lobby = _lobbyService.createLobby(validUser1Id, false, password);
        final String lobbyId = lobby.getId();

        Assertions.assertThrows(UserRegistrationException.class, () ->  _lobbyService.addPiece(validUser2Id, lobbyId)); // User not in lobby cant modify board in any way

        Assertions.assertThrows(UserRegistrationException.class, () ->  _lobbyService.modifyBoardProperties(validUser2Id, lobbyId, "", 20));

        lobby = _lobbyService.joinLobby(validUser2Id, lobby.getId(), password);

        Assertions.assertEquals(0, lobby.getBoard().getPieces().size()); // There are no pieces in the board

        for (int i = 0; i < LobbyService.MAX_PIECES_PER_BOARDS - 1; i++) {
            Assertions.assertDoesNotThrow(() -> _lobbyService.addPiece(validUser2Id, lobbyId)); // Add pieces
        }

        lobby = _lobbyService.addPiece(validUser2Id, lobbyId);

        Assertions.assertThrows(InvalidOperationException.class, () -> _lobbyService.addPiece(validUser2Id, lobbyId)); // User is unable to add more pieces than the max

        Assertions.assertNotEquals(0, lobby.getBoard().getPieces().size()); // There is one pieces in the board

        Piece piece = lobby.getBoard().getPieces().getFirst();
        int newHp = 50;
        piece.setHp(newHp);

        lobby = _lobbyService.updatePiece(validUser1Id, lobbyId, piece); // Update piece
        final Piece constPiece = lobby.getBoard().getPieces().getFirst();

        Assertions.assertEquals(newHp, constPiece.getHp());

        Assertions.assertDoesNotThrow(() -> _lobbyService.removePiece(validUser1Id, lobbyId, constPiece));

        Assertions.assertThrows(InvalidOperationException.class, () -> _lobbyService.removePiece(validUser1Id, lobbyId, piece));
    }

    @Test
    @DirtiesContext
    public void boardPersistenceOperations() {
        String validUser1Id = "random1@gmail.com";
        String validUser2Id = "random2@gmail.com";

        String password = "secret";
        Lobby lobby = _lobbyService.createLobby(validUser1Id, false, password);
        lobby = _lobbyService.joinLobby(validUser2Id, lobby.getId(), password);

        lobby = _lobbyService.addPiece(validUser1Id, lobby.getId());

        Piece piece = lobby.getBoard().getPieces().getFirst();

        final Board board = lobby.getBoard();

        for (int i = 0; i < LobbyService.MAX_BOARDS_PER_USER; i++) {
            Assertions.assertDoesNotThrow(() -> _lobbyService.addBoard(validUser1Id, board));
        }

        Assertions.assertThrows(BoardRegistrationException.class, () -> _lobbyService.addBoard(validUser1Id, board)); // User tries to add more than the permitted amount

        List<Board> savedBoards = _lobbyService.getSavedBoards(validUser1Id);

        Assertions.assertEquals(LobbyService.MAX_BOARDS_PER_USER, savedBoards.size());

        savedBoards.getLast().setId("otherId");

        Assertions.assertThrows(BoardRegistrationException.class, () -> _lobbyService.saveBoard(validUser1Id, savedBoards.getLast())); // Save not persistent board

        Assertions.assertThrows(InvalidOperationException.class, () -> _lobbyService.saveBoard(validUser2Id, savedBoards.getFirst())); // Save valid board from other user

        Assertions.assertDoesNotThrow(() -> _lobbyService.removeBoard(validUser1Id, savedBoards.getFirst())); // Remove board

        List<Board> savedBoards2 = _lobbyService.getSavedBoards(validUser1Id);

        Assertions.assertNotEquals(LobbyService.MAX_BOARDS_PER_USER, savedBoards2.size()); // Check that it has been removed
    }
}