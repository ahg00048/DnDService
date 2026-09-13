package es.ujaen.ahg00048.microservice_lobby.rest;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.exception.BoardRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.exception.InvalidOperationException;
import es.ujaen.ahg00048.microservice_lobby.exception.LobbyRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.exception.UserRegistrationException;
import es.ujaen.ahg00048.microservice_lobby.rest.DTO.LobbyDTO;
import es.ujaen.ahg00048.microservice_lobby.rest.DTO.boardGame.BoardDTO;
import es.ujaen.ahg00048.microservice_lobby.rest.mapper.LobbyMapper;
import es.ujaen.ahg00048.microservice_lobby.service.LobbyService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lobbies")
public class LobbyController {
    @Autowired
    private LobbyService _lobbyService;

    @Autowired
    private LobbyMapper _mapper;


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public void validationConstraintViolationException() {}


    /// Lobbies logic -----------------------------------------------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<Integer> getMaxUsersPerLobby() {
        return ResponseEntity.ok(_lobbyService.MAX_USERS_PER_LOBBY());
    }

    @PostMapping
    public ResponseEntity<LobbyDTO> createLobby(@RequestParam(value = "userId", required = true) String userId,
                                                @RequestParam(value = "open", required = true) boolean open,
                                                @RequestParam(value = "password", required = true) String password) {
        try {
            return ResponseEntity.ok(_mapper.dto(_lobbyService.createLobby(userId, open, password)));
        } catch (LobbyRegistrationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LobbyDTO> joinLobby(@RequestParam(value = "userId", required = true) String userId,
                                              @RequestParam(value = "password", required = true) String password,
                                              @PathVariable String id) {
        try {
            return ResponseEntity.ok(_mapper.dto(_lobbyService.joinLobby(userId, id, password)));
        } catch (LobbyRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> leaveLobby(@RequestParam(value = "userId", required = true) String userId,
                                           @PathVariable String id) {
        try {
            _lobbyService.leaveLobby(userId, id);
            return ResponseEntity.ok().build();
        } catch (LobbyRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /// Boards game logic -----------------------------------------------------------------------------------------------------------

    @PostMapping("/{id}/")
    public ResponseEntity<Lobby> addPiece(@RequestParam(value = "userId", required = true) String userId,
                                          @PathVariable String id) {
        return ResponseEntity.ok().build();
    }


    /// Boards persistence logic -----------------------------------------------------------------------------------------------------------

    @GetMapping("/boards/MaxAllowedPerUser")
    public ResponseEntity<Integer> getMaxBoardsPerUserAllowed() {
        return ResponseEntity.ok(_lobbyService.MAX_PIECES_PER_BOARDS());
    }

    @GetMapping("/boards")
    public ResponseEntity<List<BoardDTO>> getSavedBoards(@RequestParam(value = "userId", required = true) String userId) {
        return ResponseEntity.ok(_lobbyService.getSavedBoards(userId).stream().map(b -> _mapper.dto(b)).toList());
    }

    @PostMapping("/boards")
    public ResponseEntity<BoardDTO> addBoard(@RequestParam(value = "userId", required = true) String userId,
                                             @RequestBody BoardDTO boardDTO) {
        try {
            return ResponseEntity.ok(_mapper.dto(_lobbyService.addBoard(userId, _mapper.entity(boardDTO))));
        } catch (BoardRegistrationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/boards/{id}")
    public ResponseEntity<BoardDTO> saveBoard(@RequestParam(value = "userId", required = true) String userId,
                                              @PathVariable String id,
                                              @RequestBody BoardDTO boardDTO) {
        try {
            return ResponseEntity.ok(_mapper.dto(_lobbyService.saveBoard(userId, id, _mapper.entity(boardDTO))));
        } catch (BoardRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/boards/{id}")
    public ResponseEntity<Void> removeBoard(@RequestParam(value = "userId", required = true) String userId,
                                            @PathVariable String id) {
        try {
            _lobbyService.removeBoard(userId, id);
            return ResponseEntity.ok().build();
        } catch (BoardRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}