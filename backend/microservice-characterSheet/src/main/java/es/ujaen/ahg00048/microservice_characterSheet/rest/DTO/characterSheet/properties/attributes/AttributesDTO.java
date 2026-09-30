package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.attributes;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.attributes.Attributes;

public record AttributesDTO(AttributeDTO strength, AttributeDTO dexterity, AttributeDTO constitution,
                            AttributeDTO intelligence, AttributeDTO wisdom, AttributeDTO charisma) {
    public AttributesDTO(Attributes attributes) {
        this(new AttributeDTO(attributes.getStrength()), new AttributeDTO(attributes.getDexterity()), new AttributeDTO(attributes.getConstitution()),
                new AttributeDTO(attributes.getIntelligence()), new AttributeDTO(attributes.getWisdom()), new AttributeDTO(attributes.getCharisma()));
    }
}
