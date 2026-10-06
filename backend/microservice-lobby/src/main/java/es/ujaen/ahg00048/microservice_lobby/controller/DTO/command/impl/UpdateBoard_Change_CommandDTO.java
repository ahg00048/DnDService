package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.CommandType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.boardGame.BoardDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;


@Getter
@Setter
public class UpdateBoard_Change_CommandDTO extends ACommandDTO {
    private String boardId;


    public UpdateBoard_Change_CommandDTO(String userId, CommandType type, String boardId) {
        super(userId, type);
        this.boardId = boardId;
    }
}
