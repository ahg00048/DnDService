package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.CommandType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;


@Getter
@Setter
public class UpdateBoard_Image_Scale_CommandDTO extends ACommandDTO {
    private String imageId;
    private int scale;


    public UpdateBoard_Image_Scale_CommandDTO(String userId, CommandType type, String imageId, int scale) {
        super(userId, type);
        this.imageId = imageId;
        this.scale = scale;
    }
}
