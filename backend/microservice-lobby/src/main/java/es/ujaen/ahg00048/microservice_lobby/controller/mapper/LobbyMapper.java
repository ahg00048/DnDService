package es.ujaen.ahg00048.microservice_lobby.controller.mapper;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Piece;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.LobbyDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame.BoardDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame.PieceDTO;
import org.springframework.stereotype.Service;


@Service
public class LobbyMapper {
    public LobbyDTO dto(Lobby lobby) {
        return new LobbyDTO(lobby);
    }

    public BoardDTO dto(Board board) {
        return new BoardDTO(board);
    }

    public Lobby entity(LobbyDTO lobbyDTO) {
        return new Lobby(lobbyDTO.id(), lobbyDTO.open(), lobbyDTO.password(), entity(lobbyDTO.board()), lobbyDTO.users());
    }

    public Board entity(BoardDTO boardDTO) {
        return new Board("", boardDTO.id(), boardDTO.backgroundImage(), boardDTO.scale(), boardDTO.pieces().stream().map(this::entity).toList());
    }

    public Piece entity(PieceDTO pieceDTO) {
        return new Piece(pieceDTO.id(), "", pieceDTO.x(), pieceDTO.y(), pieceDTO.imageId(), pieceDTO.hp(), pieceDTO.maxHp());
    }
}
