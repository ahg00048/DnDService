package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AddPiece_ClearBoard_CommandDTO.class, name = "ADD_PIECE"),
        @JsonSubTypes.Type(value = Select_Deselect_Remove_Piece_CommandDTO.class, name = "RM_PIECE"),

        @JsonSubTypes.Type(value = UpdatePiece_HpMaxHp_CommandDTO.class, name = "UPDATE_PIECE_HP_MAX_HP"),
        @JsonSubTypes.Type(value = UpdatePiece_Image_CommandDTO.class, name = "UPDATE_PIECE_IMAGE"),
        @JsonSubTypes.Type(value = UpdatePiece_Pos_CommandDTO.class, name = "UPDATE_PIECE_POS"),

        @JsonSubTypes.Type(value = Select_Deselect_Remove_Piece_CommandDTO.class, name = "SELECT_PIECE"),
        @JsonSubTypes.Type(value = Select_Deselect_Remove_Piece_CommandDTO.class, name = "DESELECT_PIECE"),

        @JsonSubTypes.Type(value = UpdateBoard_Change_CommandDTO.class, name = "UPDATE_BOARD"),
        @JsonSubTypes.Type(value = UpdateBoard_Image_Scale_CommandDTO.class, name = "UPDATE_BOARD_IMAGE_SCALE"),
        @JsonSubTypes.Type(value = AddPiece_ClearBoard_CommandDTO.class, name = "UPDATE_BOARD_CLEAR")
})
public abstract class ACommandDTO {
    public String userId = "";
    public CommandType type = CommandType.ADD_PIECE;
}