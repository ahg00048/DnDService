package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties;


import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.PassiveEffect;

public record PassiveEffectDTO(String effect, String source) {
    public PassiveEffectDTO(PassiveEffect passiveEffect) {
        this(passiveEffect.getEffect(), passiveEffect.getSource());
    }
}

