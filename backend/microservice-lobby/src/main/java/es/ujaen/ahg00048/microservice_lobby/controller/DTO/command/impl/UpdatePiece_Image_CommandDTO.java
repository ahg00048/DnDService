package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.CommandType;
import lombok.Getter;
import lombok.Setter;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame.PieceDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;


@Getter
@Setter
public class UpdatePiece_Image_CommandDTO extends ACommandDTO {
    private int pieceId;
    private String imageId;


    public UpdatePiece_Image_CommandDTO(String userId, CommandType type, int pieceId, String imageId) {
        super(userId, type);
        this.pieceId = pieceId;
        this.imageId = imageId;
    }
}
