package es.ujaen.ahg00048.microservice_lobby.controller;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.lobby.LobbyDTO;
import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl.*;
import es.ujaen.ahg00048.microservice_lobby.controller.mapper.LobbyMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import es.ujaen.ahg00048.microservice_lobby.service.LobbyService;


@Slf4j
@Controller
public class LobbyWsController {
    @Autowired
    private LobbyService _lobbyService;

    @Autowired
    private LobbyMapper _mapper;


    @MessageExceptionHandler(RuntimeException.class)
    public void genericMessageExceptionHandler() {}

    @SubscribeMapping("/lobbies/{id}")
    @SendTo("/topic/lobbies/{id}")
    public LobbyDTO subscribeToLobby(@DestinationVariable String id) throws RuntimeException {
        return _mapper.dto(_lobbyService.getLobby(id));
    }

    @MessageMapping("/lobbies/{id}")
    public LobbyDTO sendCommand(@DestinationVariable String id, @Valid @Payload ACommandDTO command) throws RuntimeException {
        if (command instanceof AddPiece_ClearBoard_CommandDTO subCommand) {
            switch (subCommand.getType()) {
                case ADD_PIECE:
                    return  _mapper.dto(_lobbyService.addPiece(subCommand.getUserId(), id));
                case UPDATE_BOARD_CLEAR:
                    return _mapper.dto(_lobbyService.clearBoard(subCommand.getUserId(), id));
            }
        } else if (command instanceof Select_Deselect_Remove_Piece_CommandDTO subCommand) {
            switch (subCommand.getType()) {
                case RM_PIECE:
                    return _mapper.dto(_lobbyService.removePiece(subCommand.getUserId(), id,
                            _mapper.entity(subCommand.getPiece())));
                case SELECT_PIECE:
                    return _mapper.dto(_lobbyService.selectPiece(subCommand.getUserId(), id,
                            _mapper.entity(subCommand.getPiece())));
                case DESELECT_PIECE:
                    return _mapper.dto(_lobbyService.deselectPiece(subCommand.getUserId(), id,
                            _mapper.entity(subCommand.getPiece())));
            }
        } else if (command instanceof UpdateBoard_Change_CommandDTO subCommand) {
            return _mapper.dto(_lobbyService.changeBoard(subCommand.getUserId(), id,
                    _mapper.entity(subCommand.getBoard())));
        } else if (command instanceof UpdateBoard_Image_Scale_CommandDTO subCommand) {
            return _mapper.dto(_lobbyService.modifyBoardProperties(subCommand.getUserId(), id,
                    subCommand.getImageId(), subCommand.getScale()));
        } else if (command instanceof UpdatePiece_HpMaxHp_CommandDTO subCommand) {
            return _mapper.dto(_lobbyService.updatePieceProperties_hp_maxHp(subCommand.getUserId(), id,
                    _mapper.entity(subCommand.getPiece()), subCommand.getHp(), subCommand.getMaxHp()));
        } else if (command instanceof UpdatePiece_Image_CommandDTO subCommand) {
            return _mapper.dto(_lobbyService.updatePieceProperties_image(subCommand.getUserId(), id,
                    _mapper.entity(subCommand.getPiece()), subCommand.getImageId()));
        } else if (command instanceof UpdatePiece_Pos_CommandDTO subCommand) {
            return _mapper.dto(_lobbyService.updatePieceProperties_pos(subCommand.getUserId(), id,
                    _mapper.entity(subCommand.getPiece()), subCommand.getX(), subCommand.getY()));
        }

        return _mapper.dto(_lobbyService.getLobby(id));
    }
}
