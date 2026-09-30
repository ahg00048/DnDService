package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.saves;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.saves.Save;

public record SaveDTO(boolean proficient, int modifier) {
    public SaveDTO(Save save) {
        this(save.isProficient(), save.getModifier());
    }
}
