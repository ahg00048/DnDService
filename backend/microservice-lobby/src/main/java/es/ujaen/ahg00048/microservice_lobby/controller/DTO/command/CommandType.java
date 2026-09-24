package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command;

public enum CommandType {
    ADD_PIECE,
    RM_PIECE,

    UPDATE_PIECE_HP_MAX_HP,
    UPDATE_PIECE_IMAGE,
    UPDATE_PIECE_POS,

    SELECT_PIECE,
    DESELECT_PIECE,

    UPDATE_BOARD,
    UPDATE_BOARD_IMAGE_SCALE,
    UPDATE_BOARD_CLEAR
}
