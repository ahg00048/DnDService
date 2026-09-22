package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame.PieceDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;


@Getter
@Setter
@AllArgsConstructor
public class Select_Deselect_Remove_Piece_CommandDTO extends ACommandDTO {
    private PieceDTO piece;
}
