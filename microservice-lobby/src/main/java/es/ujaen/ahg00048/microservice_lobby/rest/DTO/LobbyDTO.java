package es.ujaen.ahg00048.microservice_lobby.rest.DTO;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.rest.DTO.boardGame.BoardDTO;

import java.util.List;

public record LobbyDTO(String id, boolean open, String password, BoardDTO board, List<String> users) {
    public LobbyDTO(Lobby lobby) {
        this(lobby.getId(), lobby.isOpen(), lobby.getPassword(), new BoardDTO(lobby.getBoard()), lobby.getUsersIds());
    }
}
