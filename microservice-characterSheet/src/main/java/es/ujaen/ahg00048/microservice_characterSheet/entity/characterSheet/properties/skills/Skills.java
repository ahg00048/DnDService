package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.skills;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Skills {
    private final Skill acrobatics = new Skill();
    private final Skill sleight_of_hand = new Skill();
    private final Skill stealth = new Skill();

    private final Skill athletics = new Skill();

    private final Skill arcana = new Skill();
    private final Skill history = new Skill();
    private final Skill investigation = new Skill();
    private final Skill nature = new Skill();
    private final Skill religion = new Skill();

    private final Skill animal_handling = new Skill();
    private final Skill perception = new Skill();
    private final Skill insight = new Skill();
    private final Skill survival = new Skill();
    private final Skill medicine = new Skill();

    private final Skill performance = new Skill();
    private final Skill deception = new Skill();
    private final Skill intimidation = new Skill();
    private final Skill persuasion = new Skill();
}
