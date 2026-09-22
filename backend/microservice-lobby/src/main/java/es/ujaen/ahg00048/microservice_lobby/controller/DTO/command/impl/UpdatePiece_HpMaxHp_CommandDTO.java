package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame.PieceDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;


@Getter
@Setter
@AllArgsConstructor
public class UpdatePiece_HpMaxHp_CommandDTO extends ACommandDTO {
    private PieceDTO piece;
    private int hp;
    private int maxHp;
}
