package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.saves;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.saves.Saves;

public record SavesDTO(SaveDTO strength, SaveDTO dexterity, SaveDTO constitution, SaveDTO intelligence, SaveDTO wisdom, SaveDTO charisma) {
    public SavesDTO(Saves saves) {
        this(new SaveDTO(saves.getStrength()), new SaveDTO(saves.getDexterity()), new SaveDTO(saves.getConstitution()),
                new SaveDTO(saves.getIntelligence()), new SaveDTO(saves.getWisdom()), new SaveDTO(saves.getCharisma()));
    }
}
