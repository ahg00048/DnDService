package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.skills;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.skills.Skill;

public record SkillDTO(boolean proficient, int value, int modifier) {
    public SkillDTO(Skill skill) {
        this(skill.isProficient(), skill.getValue(), skill.getModifier());
    }
}
