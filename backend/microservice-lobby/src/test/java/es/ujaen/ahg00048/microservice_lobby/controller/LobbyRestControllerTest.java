package es.ujaen.ahg00048.microservice_lobby.controller;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.LobbyReqBodyDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.LobbyDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame.BoardDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.mapper.LobbyMapper;
import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;


import java.util.ArrayList;
import java.util.List;


@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = es.ujaen.ahg00048.microservice_lobby.app.MicroserviceLobbyApplication.class)
@ContextConfiguration
@WebAppConfiguration
public class LobbyRestControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc _mvc;

    @Autowired
    private ObjectMapper _oMapper;

    @Autowired
    private LobbyMapper _mapper;

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

    @BeforeEach
    public void setup() {
        _mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DirtiesContext
    public void boardPersistenceTest() throws Exception {
        String validUserId1 = "random1-id";
        String nameUser1 = "user1";
        String validUserId2 = "random2-id";
        String nameUser2 = "user2";

        MvcResult result = _mvc.perform(
            post("/api/v1/lobbies")
            .with(jwt().jwt((jwt -> { jwt.subject(validUserId1); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser1); } )))
            .contentType(MediaType.APPLICATION_JSON)
            .content(_oMapper.writeValueAsString(new LobbyReqBodyDTO(true, "")))
        ).andExpect(status().isOk())
        .andReturn();

        LobbyDTO lobbyDTO = _oMapper.readValue(result.getResponse().getContentAsString(), LobbyDTO.class);

        result = _mvc.perform(
            get("/api/v1/lobbies/" + lobbyDTO.id() + "?password=")
            .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
        ).andExpect(status().isOk())
        .andReturn();

        lobbyDTO = _oMapper.readValue(result.getResponse().getContentAsString(), LobbyDTO.class);

        int maxBoardsPerUser = Integer.parseInt(_mvc.perform(
            get("/api/v1/lobbies/boards/MaxAllowedPerUser")
            .with(jwt().jwt((jwt -> { jwt.subject(validUserId1); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser1); } )))
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString());

        List<BoardDTO> boardDTOs = new ArrayList<>();

        for (int i = 0; i < maxBoardsPerUser; i++) {
            result = _mvc.perform(
                    post("/api/v1/lobbies/boards")
                    .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(_oMapper.writeValueAsString(lobbyDTO.board()))
            ).andExpect(status().isOk())
            .andReturn();

            boardDTOs.add(_oMapper.readValue(result.getResponse().getContentAsString(), BoardDTO.class));
        }

        // Try to save more than the number allowed
        _mvc.perform(
                post("/api/v1/lobbies/boards")
                        .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(_oMapper.writeValueAsString(lobbyDTO.board()))
        ).andExpect(status().isConflict());


        Board board = _mapper.entity(lobbyDTO.board());

        // Try to save registered board from other user
        _mvc.perform(
                put("/api/v1/lobbies/boards/" + boardDTOs.getFirst().id())
                        .with(jwt().jwt((jwt -> { jwt.subject(validUserId1); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser1); } )))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(_oMapper.writeValueAsString(board))
        ).andExpect(status().isForbidden());

        // Try to save unregistered board
        _mvc.perform(
                put("/api/v1/lobbies/boards/invalidId")
                        .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(_oMapper.writeValueAsString(board))
        ).andExpect(status().isNotFound());

        // Save board correctly
        _mvc.perform(
                put("/api/v1/lobbies/boards/" + boardDTOs.getFirst().id())
                        .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(_oMapper.writeValueAsString(board))
        ).andExpect(status().isOk());

        // Try to remove registered board from other user
        _mvc.perform(
                delete("/api/v1/lobbies/boards/" + boardDTOs.getFirst().id())
                        .with(jwt().jwt((jwt -> { jwt.subject(validUserId1); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser1); } )))
        ).andExpect(status().isForbidden());

        // Try to remove unregistered board
        _mvc.perform(
                delete("/api/v1/lobbies/boards/invalidID")
                        .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
        ).andExpect(status().isNotFound());

        // remove board correctly
        _mvc.perform(
                delete("/api/v1/lobbies/boards/" + boardDTOs.getFirst().id())
                        .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
        ).andExpect(status().isOk());
    }

    @Test
    @DirtiesContext
    public void lobbiesInteractionTest() throws Exception {
        String validUserId1 = "random1-id";
        String nameUser1 = "user1";
        String validUserId2 = "random2-id";
        String nameUser2 = "user2";

        // Try to join to unregistered lobby
        _mvc.perform(
            get("/api/v1/lobbies/invalidId?password=")
            .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
        ).andExpect(status().isNotFound());

        String password = "secret";

        // Create lobby

        MvcResult result = _mvc.perform(
            post("/api/v1/lobbies")
            .with(jwt().jwt((jwt -> { jwt.subject(validUserId1); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser1); } )))
            .contentType(MediaType.APPLICATION_JSON)
            .content(_oMapper.writeValueAsString(new LobbyReqBodyDTO(true, "")))
        ).andExpect(status().isOk())
        .andReturn();

        LobbyDTO lobbyDTO = _oMapper.readValue(result.getResponse().getContentAsString(), LobbyDTO.class);

        // Try to create lobby while registered in one
        _mvc.perform(
            post("/api/v1/lobbies")
            .with(jwt().jwt((jwt -> { jwt.subject(validUserId1); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser1); } )))
            .contentType(MediaType.APPLICATION_JSON)
            .content(_oMapper.writeValueAsString(new LobbyReqBodyDTO(true, password)))
        ).andExpect(status().isConflict());

        // Join lobby
        result = _mvc.perform(
            get("/api/v1/lobbies/" + lobbyDTO.id() + "?password=" + password)
            .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
        ).andExpect(status().isOk())
        .andReturn();

        lobbyDTO = _oMapper.readValue(result.getResponse().getContentAsString(), LobbyDTO.class);

        // Try to join in already joined lobby
        _mvc.perform(
            get("/api/v1/lobbies/" + lobbyDTO.id() + "?password=" + password)
            .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
        ).andExpect(status().isConflict());

        // Try to leave to unregistered lobby
        _mvc.perform(
                put("/api/v1/lobbies/invalidId?")
                .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
        ).andExpect(status().isNotFound());

        // leave lobby
        _mvc.perform(
                put("/api/v1/lobbies/" + lobbyDTO.id())
                .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
        ).andExpect(status().isOk());

        // Try to leave already left lobby
        _mvc.perform(
                put("/api/v1/lobbies/" + lobbyDTO.id())
                .with(jwt().jwt((jwt -> { jwt.subject(validUserId2); jwt.claim(LobbyRestController.CLAIM_USER_NAME, nameUser2); } )))
        ).andExpect(status().isConflict());
    }
}
