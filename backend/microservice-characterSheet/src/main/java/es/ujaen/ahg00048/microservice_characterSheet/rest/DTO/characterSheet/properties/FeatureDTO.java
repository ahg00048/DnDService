package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.Feature;

public record FeatureDTO(String name, String description) {
    public FeatureDTO(Feature feature) {
        this(feature.getName(), feature.getDescription());
    }
}
