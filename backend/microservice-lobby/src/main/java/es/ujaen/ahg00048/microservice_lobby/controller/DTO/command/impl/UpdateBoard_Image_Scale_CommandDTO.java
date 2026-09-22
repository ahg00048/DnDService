package es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import es.ujaen.ahg00048.microservice_lobby.controller.DTO.command.ACommandDTO;


@Getter
@Setter
@AllArgsConstructor
public class UpdateBoard_Image_Scale_CommandDTO extends ACommandDTO {
    private String imageId;
    private int scale;
}
