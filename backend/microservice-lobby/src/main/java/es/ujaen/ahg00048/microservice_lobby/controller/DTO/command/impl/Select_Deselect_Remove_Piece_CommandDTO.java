package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.CommandType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame.PieceDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;


@Getter
@Setter
public class Select_Deselect_Remove_Piece_CommandDTO extends ACommandDTO {
    private PieceDTO piece;


    public Select_Deselect_Remove_Piece_CommandDTO(String userId, CommandType type, PieceDTO piece) {
        super(userId, type);
        this.piece = piece;
    }
}
