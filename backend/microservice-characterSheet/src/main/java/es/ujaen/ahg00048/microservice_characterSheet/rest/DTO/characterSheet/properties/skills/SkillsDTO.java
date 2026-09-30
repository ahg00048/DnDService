package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.skills;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.skills.Skills;

public record SkillsDTO(SkillDTO acrobatics, SkillDTO sleight_of_hand, SkillDTO stealth, SkillDTO athletics, SkillDTO arcana, SkillDTO history, SkillDTO investigation,
                        SkillDTO nature, SkillDTO religion, SkillDTO animal_handling, SkillDTO perception, SkillDTO insight, SkillDTO survival, SkillDTO medicine,
                        SkillDTO performance, SkillDTO deception, SkillDTO intimidation, SkillDTO persuasion) {
    public SkillsDTO(Skills skills) {
        this(new SkillDTO(skills.getAcrobatics()), new SkillDTO(skills.getSleight_of_hand()), new SkillDTO(skills.getStealth()), new SkillDTO(skills.getAthletics()),
                new SkillDTO(skills.getArcana()), new SkillDTO(skills.getHistory()), new SkillDTO(skills.getInvestigation()), new SkillDTO(skills.getNature()),
                new SkillDTO(skills.getReligion()), new SkillDTO(skills.getAnimal_handling()), new SkillDTO(skills.getPerception()), new SkillDTO(skills.getInsight()),
                new SkillDTO(skills.getSurvival()), new SkillDTO(skills.getMedicine()), new SkillDTO(skills.getPerformance()), new SkillDTO(skills.getDeception()),
                new SkillDTO(skills.getIntimidation()), new SkillDTO(skills.getPersuasion()));
    }
}
