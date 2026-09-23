package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.CommandType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame.PieceDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;


@Getter
@Setter
public class UpdatePiece_Pos_CommandDTO extends ACommandDTO {
    private PieceDTO piece;
    private float x;
    private float y;


    public UpdatePiece_Pos_CommandDTO(String userId, CommandType type, PieceDTO piece, float x, float y) {
        super(userId, type);
        this.piece = piece;
        this.x = x;
        this.y = y;
    }
}
