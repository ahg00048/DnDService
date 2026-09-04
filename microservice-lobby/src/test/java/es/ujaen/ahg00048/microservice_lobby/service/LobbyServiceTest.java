package es.ujaen.ahg00048.microservice_lobby.service;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.exception.LobbyRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.exception.UserRegistrationException;
import jakarta.validation.ConstraintViolationException;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = es.ujaen.ahg00048.microservice_lobby.app.MicroserviceLobbyApplication.class)
@ActiveProfiles("test")

public class LobbyServiceTest {
    @Autowired
    private LobbyService _lobbyService;


    @Test
    @DirtiesContext
    public void joinAndLeaveLobby() {
        String validUserId1 = "random1@gmail.com";
        String validUserId2 = "random2@gmail.com";

        Assertions.assertThrows(ConstraintViolationException.class, () -> _lobbyService.createLobby("aaa", false, "secret")); // create lobby with invalid user id

        Lobby lobby = _lobbyService.createLobby(validUserId1, false, "secret"); // Create lobby

        Assertions.assertThrows(LobbyRegistrationException.class, () -> _lobbyService.createLobby(validUserId1, false, "secret")); // Trying to create a lobby while being in one

        Assertions.assertDoesNotThrow(() -> _lobbyService.joinLobby(validUserId2, lobby.getId())); // join lobby

        Assertions.assertThrows(UserRegistrationException.class, () -> _lobbyService.joinLobby(validUserId2, lobby.getId()));  // Trying to join into a lobby already joined

        Assertions.assertDoesNotThrow(() -> _lobbyService.leaveLobby(validUserId2, lobby.getId())); // User 2 leaves Lobby

        Assertions.assertThrows(UserRegistrationException.class, () -> _lobbyService.leaveLobby(validUserId2, lobby.getId())); // User 2 tries to leave the lobby again

        Assertions.assertDoesNotThrow(() -> _lobbyService.leaveLobby(validUserId1, lobby.getId())); // User 2 leaves Lobby

        Assertions.assertThrows(LobbyRegistrationException.class, () -> _lobbyService.joinLobby(validUserId1, lobby.getId())); // user trying to join nonexisting lobby
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

    }

    @Test
    @DirtiesContext
    public void boardPersistenceOperations() {

    }
}