package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.Spell;

public record SpellDTO(String type, String name, String description, int range, String target, String components, String castTime, String duration, String roll) {
    public SpellDTO(Spell spell) {
        this(spell.getType(), spell.getName(), spell.getDescription(), spell.getRange(), spell.getTarget(), spell.getComponents(), spell.getCastTime(), spell.getDuration(), spell.getRoll());
    }
}
