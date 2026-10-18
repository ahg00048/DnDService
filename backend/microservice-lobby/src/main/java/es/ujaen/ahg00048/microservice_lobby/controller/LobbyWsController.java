package es.ujaen.ahg00048.microservice_lobby.controller;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.LobbyDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl.*;
import es.ujaen.ahg00048.microservice_lobby.controller.mapper.LobbyMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;

import es.ujaen.ahg00048.microservice_lobby.service.LobbyService;

import java.security.Principal;


@Slf4j
@Controller
public class LobbyWsController {
    @Autowired
    private LobbyService _lobbyService;

    @Autowired
    private LobbyMapper _mapper;


    @MessageExceptionHandler(RuntimeException.class)
    public void genericMessageExceptionHandler() {}

    @MessageMapping("/lobbies.{id}")
    @SendTo("/topic/lobbies.{id}")
    public LobbyDTO sendCommand(@DestinationVariable String id,
                                Principal auth,
                                @Valid @Payload ACommandDTO command) throws RuntimeException {
        if (command instanceof AddPiece_ClearBoard_CommandDTO subCommand) {
            switch (subCommand.getType()) {
                case ADD_PIECE:
                    return _mapper.dto(_lobbyService.addPiece(auth.getName(), id));
                case UPDATE_BOARD_CLEAR:
                    return _mapper.dto(_lobbyService.clearBoard(auth.getName(), id));
            }
        } else if (command instanceof Select_Deselect_Remove_Piece_CommandDTO subCommand) {
            switch (subCommand.getType()) {
                case RM_PIECE:
                    return _mapper.dto(_lobbyService.removePiece(auth.getName(), id,
                            subCommand.getPieceId()));
                case SELECT_PIECE:
                    return _mapper.dto(_lobbyService.selectPiece(auth.getName(), id,
                            subCommand.getPieceId()));
                case DESELECT_PIECE:
                    return _mapper.dto(_lobbyService.deselectPiece(auth.getName(), id,
                            subCommand.getPieceId()));
            }
        } else if (command instanceof UpdateBoard_Change_CommandDTO subCommand) {
            return _mapper.dto(_lobbyService.changeBoard(auth.getName(), id,
                    subCommand.getBoardId()));
        } else if (command instanceof UpdateBoard_Image_Scale_CommandDTO subCommand) {
            return _mapper.dto(_lobbyService.modifyBoardProperties(auth.getName(), id,
                    subCommand.getImageId(), subCommand.getScale()));
        } else if (command instanceof UpdatePiece_HpMaxHp_CommandDTO subCommand) {
            return _mapper.dto(_lobbyService.updatePieceProperties_hp_maxHp(auth.getName(), id,
                    subCommand.getPieceId(), subCommand.getHp(), subCommand.getMaxHp()));
        } else if (command instanceof UpdatePiece_Image_CommandDTO subCommand) {
            return _mapper.dto(_lobbyService.updatePieceProperties_image(auth.getName(), id,
                    subCommand.getPieceId(), subCommand.getImageId()));
        } else if (command instanceof UpdatePiece_Pos_CommandDTO subCommand) {
            return _mapper.dto(_lobbyService.updatePieceProperties_pos(auth.getName(), id,
                    subCommand.getPieceId(), subCommand.getX(), subCommand.getY()));
        }

        return _mapper.dto(_lobbyService.getLobby(id));
    }
}
