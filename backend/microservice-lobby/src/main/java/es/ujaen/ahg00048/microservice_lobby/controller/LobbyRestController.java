package es.ujaen.ahg00048.microservice_lobby.controller;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.LobbyReqBodyDTO;
import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.exception.BoardRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.exception.InvalidOperationException;
import es.ujaen.ahg00048.microservice_lobby.exception.LobbyRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.exception.UserRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.LobbyDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame.BoardDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.mapper.LobbyMapper;
import es.ujaen.ahg00048.microservice_lobby.service.LobbyService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/lobbies")
public class LobbyRestController {
    @Autowired
    private LobbyService _lobbyService;

    @Autowired
    private LobbyMapper _mapper;

    @Autowired
    private SimpMessagingTemplate _simpTemplate;

    @Value("${app.lobby.message.unregistered}")
    private String _message_lobbyUnregistered;

    public static final String CLAIM_USER_NAME = "preferred_username";


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public void validationConstraintViolationException() {}


    /// Lobbies logic -----------------------------------------------------------------------------------------------------------

    @GetMapping("/MaxUsersPerLobby")
    public ResponseEntity<Integer> getMaxUsersPerLobby() {
        return ResponseEntity.ok(_lobbyService.MAX_USERS_PER_LOBBY());
    }

    @GetMapping
    public ResponseEntity<List<LobbyDTO>> getPublicLobbies() {
        return ResponseEntity.ok(_lobbyService.getPublicLobbies().stream().map(l -> _mapper.dto(l)).toList());
    }

    @PostMapping
    public ResponseEntity<LobbyDTO> createLobby(@AuthenticationPrincipal Jwt auth,
                                                @RequestBody LobbyReqBodyDTO reqBody) {
        try {
            return ResponseEntity.ok(_mapper.dto(_lobbyService.createLobby(auth.getSubject(), auth.getClaimAsString(CLAIM_USER_NAME), reqBody.open(), reqBody.password())));
        } catch (LobbyRegistrationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LobbyDTO> joinLobby(@AuthenticationPrincipal Jwt auth,
                                              @RequestParam(value = "password", required = true) String password,
                                              @PathVariable String id) {
        try {
            LobbyDTO lobbyDTO = _mapper.dto(_lobbyService.joinLobby(auth.getSubject(), auth.getClaimAsString(CLAIM_USER_NAME), id, password));
            _simpTemplate.convertAndSend("/topic/lobbies." + lobbyDTO.id(), lobbyDTO);
            return ResponseEntity.ok(lobbyDTO);
        } catch (LobbyRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> leaveLobby(@AuthenticationPrincipal Jwt auth,
                                           @PathVariable String id) {
        try {
            Optional<Lobby> lobbyOpt = _lobbyService.leaveLobby(auth.getSubject(), id);
            if (lobbyOpt.isPresent()) {
                Lobby lobby = lobbyOpt.get();
                _simpTemplate.convertAndSend("/topic/lobbies." + id, _mapper.dto(lobby));
            } else {
                _simpTemplate.convertAndSend("/topic/lobbies." + id, _message_lobbyUnregistered);
            }
            return ResponseEntity.ok().build();
        } catch (LobbyRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /// Boards persistence logic -----------------------------------------------------------------------------------------------------------

    @GetMapping("/boards/pieces/MaxAllowed")
    public ResponseEntity<Integer> getMaxPiecesPerBoard() {
        return ResponseEntity.ok(_lobbyService.MAX_PIECES_PER_BOARDS());
    }

    @GetMapping("/boards/MaxAllowedPerUser")
    public ResponseEntity<Integer> getMaxBoardsPerUserAllowed() {
        return ResponseEntity.ok(_lobbyService.MAX_BOARDS_PER_USER());
    }

    @GetMapping("/boards")
    public ResponseEntity<List<BoardDTO>> getSavedBoards(Authentication auth) {
        return ResponseEntity.ok(_lobbyService.getSavedBoards(auth.getName()).stream().map(b -> _mapper.dto(b)).toList());
    }

    @PostMapping("/boards")
    public ResponseEntity<BoardDTO> addBoard(@AuthenticationPrincipal Jwt auth,
                                             @RequestBody BoardDTO boardDTO) {
        try {
            return ResponseEntity.ok(_mapper.dto(_lobbyService.addBoard(auth.getSubject(), _mapper.entity(boardDTO))));
        } catch (BoardRegistrationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/boards/{id}")
    public ResponseEntity<BoardDTO> saveBoard(@AuthenticationPrincipal Jwt auth,
                                              @PathVariable String id,
                                              @RequestBody BoardDTO boardDTO) {
        try {
            return ResponseEntity.ok(_mapper.dto(_lobbyService.saveBoard(auth.getSubject(), id, _mapper.entity(boardDTO))));
        } catch (BoardRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/boards/{id}")
    public ResponseEntity<Void> removeBoard(@AuthenticationPrincipal Jwt auth,
                                            @PathVariable String id) {
        try {
            _lobbyService.removeBoard(auth.getSubject(), id);
            return ResponseEntity.ok().build();
        } catch (BoardRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}