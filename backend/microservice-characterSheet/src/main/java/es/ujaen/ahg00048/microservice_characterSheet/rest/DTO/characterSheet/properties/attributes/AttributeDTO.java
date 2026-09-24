package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.attributes;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.attributes.Attribute;

public record AttributeDTO(int value, int modifier) {
    public AttributeDTO(Attribute attribute) {
        this(attribute.getValue(), attribute.getModifier());
    }
}
