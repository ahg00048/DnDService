package es.ujaen.ahg00048.microservice_lobby.controller;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.LobbyReqBodyDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.LobbyDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame.BoardDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.mapper.LobbyMapper;
import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest(classes = es.ujaen.ahg00048.microservice_lobby.app.MicroserviceLobbyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class LobbyRestControllerTest {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private LobbyMapper _mapper;

    @Autowired
    private RestTestClient _restClient;

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
    public void boardPersistenceTest() {
        String validEmail1 = "random1@gmail.com";
        String validEmail2 = "random2@gmail.com";

        LobbyDTO lobbyDTO = _restClient.post().uri("api/v1/lobbies?userId=" + validEmail1)
                .body(new LobbyReqBodyDTO(true, ""))
                .exchange().expectStatus().isOk()
                .expectBody(LobbyDTO.class).returnResult().getResponseBody();

        lobbyDTO = _restClient.get().uri("api/v1/lobbies/" + lobbyDTO.id() + "?userId=" + validEmail2 + "&password=")
                .exchange().expectStatus().isOk()
                .expectBody(LobbyDTO.class).returnResult().getResponseBody();

        Integer maxBoardsPerUser = _restClient.get().uri("api/v1/lobbies/boards/MaxAllowedPerUser")
                .exchange().expectBody(Integer.class).returnResult().getResponseBody();

        List<BoardDTO> boardDTOs = new ArrayList<>();

        for (int i = 0; i < maxBoardsPerUser; i++) {
            boardDTOs.add(_restClient.post().uri("api/v1/lobbies/boards?userId=" + validEmail2)
                    .body(lobbyDTO.board())
                    .exchange().expectStatus().isOk()
                    .expectBody(BoardDTO.class).returnResult().getResponseBody());
        }

        // Try to save more than the number allowed
        _restClient.post().uri("api/v1/lobbies/boards?userId=" + validEmail2)
                .body(lobbyDTO.board())
                .exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT);

        // STOMP...

        // change board in lobby

        //

        for (BoardDTO btd : boardDTOs) {
          //  Assertions.assertNotEquals(lobbyDTO.board().scale(), btd.scale());
        }

        Board board = _mapper.entity(lobbyDTO.board());

        // Try to save registered board from other user
        _restClient.put().uri("api/v1/lobbies/boards/" + boardDTOs.getFirst().id() + "?userId=" + validEmail1)
                .body(_mapper.dto(board))
                .exchange().expectStatus().isForbidden();

        // Try to save unregistered board
        _restClient.put().uri("api/v1/lobbies/boards/invalidId?userId=" + validEmail2)
                .body(_mapper.dto(board))
                .exchange().expectStatus().isNotFound();

        // Save board correctly
        boardDTOs.set(0, _restClient.put().uri("api/v1/lobbies/boards/" + boardDTOs.getFirst().id() + "?userId=" + validEmail2)
                .body(_mapper.dto(board))
                .exchange().expectStatus().isOk()
                .expectBody(BoardDTO.class).returnResult().getResponseBody());

        // Try to remove registered board from other user
        _restClient.delete().uri("api/v1/lobbies/boards/" + boardDTOs.getFirst().id() + "?userId=" + validEmail1)
                .exchange().expectStatus().isForbidden();

        // Try to remove unregistered board
        _restClient.delete().uri("api/v1/lobbies/boards/invalidId?userId=" + validEmail2)
                .exchange().expectStatus().isNotFound();

        // remove board correctly
        _restClient.delete().uri("api/v1/lobbies/boards/" + boardDTOs.getFirst().id() + "?userId=" + validEmail2)
                .exchange().expectStatus().isOk();
    }

    @Test
    @DirtiesContext
    public void lobbiesInteractionTest() {
        String validEmail1 = "random1@gmail.com";
        String validEmail2 = "random2@gmail.com";

        // Try to join to unregistered lobby
        _restClient.get().uri("api/v1/lobbies/invalidId?userId=" + validEmail2 + "&password=")
                .exchange().expectStatus().isNotFound();

        String password = "secret";


        // Create lobby
        LobbyDTO lobbyDTO = _restClient.post().uri("api/v1/lobbies?userId=" + validEmail1)
                .body(new LobbyReqBodyDTO(false, password))
                .exchange().expectStatus().isOk()
                .expectBody(LobbyDTO.class).returnResult().getResponseBody();

        // Try to create lobby while registered in one
        _restClient.post().uri("api/v1/lobbies?userId=" + validEmail1)
                .body(new LobbyReqBodyDTO(false, password))
                .exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT);


        // Join lobby
        lobbyDTO = _restClient.get().uri("api/v1/lobbies/" + lobbyDTO.id() + "?userId=" + validEmail2 + "&password=" + password)
                .exchange().expectStatus().isOk()
                .expectBody(LobbyDTO.class).returnResult().getResponseBody();

        // Try to join in already joined lobby
        _restClient.get().uri("api/v1/lobbies/" + lobbyDTO.id() + "?userId=" + validEmail2 + "&password=")
                .exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT);


        // Try to leave to unregistered lobby
        _restClient.put().uri("api/v1/lobbies/invalidId?userId=" + validEmail2 + "&password=")
                .exchange().expectStatus().isNotFound();

        // leave lobby
        _restClient.put().uri("api/v1/lobbies/" + lobbyDTO.id() + "?userId=" + validEmail2 + "&password=")
                .exchange().expectStatus().isOk();

        // Try to leave already left lobby
        _restClient.put().uri("api/v1/lobbies/" + lobbyDTO.id() + "?userId=" + validEmail2 + "&password=")
                .exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DirtiesContext
    public void boardGameLogicTest() {
        String validEmail1 = "random1@gmail.com";
        String validEmail2 = "random2@gmail.com";

        LobbyDTO lobbyDTO = _restClient.post().uri("api/v1/lobbies?userId=" + validEmail1)
                .body(new LobbyReqBodyDTO(true, ""))
                .exchange().expectStatus().isOk()
                .expectBody(LobbyDTO.class).returnResult().getResponseBody();

        lobbyDTO = _restClient.get().uri("api/v1/lobbies/" + lobbyDTO.id() + "?userId=" + validEmail2 + "&password=")
                .exchange().expectStatus().isOk()
                .expectBody(LobbyDTO.class).returnResult().getResponseBody();

    }
}
