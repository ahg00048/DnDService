package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.CommandType;
import lombok.Getter;
import lombok.Setter;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;


@Getter
@Setter
public class UpdateBoard_Image_Scale_CommandDTO extends ACommandDTO {
    private String imageId;
    private int scale;


    public UpdateBoard_Image_Scale_CommandDTO(CommandType type, String imageId, int scale) {
        super(type);
        this.imageId = imageId;
        this.scale = scale;
    }
}
