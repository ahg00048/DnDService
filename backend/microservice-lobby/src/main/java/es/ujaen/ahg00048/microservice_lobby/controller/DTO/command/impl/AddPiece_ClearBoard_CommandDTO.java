package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.CommandType;

public class AddPiece_ClearBoard_CommandDTO extends ACommandDTO {
    public AddPiece_ClearBoard_CommandDTO(String userId, CommandType type) {
        super(userId, type);
    }
}
