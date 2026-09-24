package es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame;

import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;

import java.util.List;


public record BoardDTO(String id, String backgroundImage, int scale, List<PieceDTO> pieces) {
    public BoardDTO(Board board) {
        this(board.getId(), board.getBackgroundImage(), board.getScale(), board.getPieces().stream().map(PieceDTO::new).toList());
    }
}
