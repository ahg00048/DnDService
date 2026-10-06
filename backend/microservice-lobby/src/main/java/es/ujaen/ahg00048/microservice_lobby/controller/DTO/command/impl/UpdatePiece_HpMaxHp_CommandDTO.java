package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.CommandType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame.PieceDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;


@Getter
@Setter
public class UpdatePiece_HpMaxHp_CommandDTO extends ACommandDTO {
    private int pieceId;
    private int hp;
    private int maxHp;


    public UpdatePiece_HpMaxHp_CommandDTO(String userId, CommandType type, int pieceId, int hp, int maxHp) {
        super(userId, type);
        this.pieceId = pieceId;
        this.hp = hp;
        this.maxHp = maxHp;
    }
}
