package es.ujaen.ahg00048.microservice_lobby.controller;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.CommandType;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl.UpdateBoard_Image_Scale_CommandDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.LobbyDTO;
import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;


@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = es.ujaen.ahg00048.microservice_lobby.app.MicroserviceLobbyApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class LobbyWsControllerTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private WebSocketStompClient _stompClient;

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
    public void sampleTest() {
//        StompSession session = null;
//        try {
//            session = _stompClient.connectAsync("ws://localhost:" + serverPort + "/websock", new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            log.error("StompClient could not connect to endpoint");
//        }
//
//
//        // change board in lobby
//        int oldScale = lobbyDTO.board().scale();
//        int newScale = oldScale + 1;
//
//        var userSubSession = session.subscribe("/topic/lobbies-" + lobbyDTO.id(), new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return LobbyDTO.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, @Nullable Object payload) {
//                LobbyDTO stompLobbbyDTO = (LobbyDTO) payload;
//                Assertions.assertNotEquals(oldScale, stompLobbbyDTO.board().scale());
//            }
//        });
//
//        ACommandDTO commandDTO = new UpdateBoard_Image_Scale_CommandDTO(
//                validUserId2, CommandType.UPDATE_BOARD_IMAGE_SCALE,
//                lobbyDTO.board().backgroundImage(), newScale);
//        session.send("/publish/lobbies-" + lobbyDTO.id(), commandDTO);
//
//        userSubSession.unsubscribe();

    }
}
