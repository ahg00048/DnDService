package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.CommandType;
import lombok.Getter;
import lombok.Setter;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;


@Getter
@Setter
public class UpdateBoard_Change_CommandDTO extends ACommandDTO {
    private String boardId;


    public UpdateBoard_Change_CommandDTO(CommandType type, String boardId) {
        super(type);
        this.boardId = boardId;
    }
}
