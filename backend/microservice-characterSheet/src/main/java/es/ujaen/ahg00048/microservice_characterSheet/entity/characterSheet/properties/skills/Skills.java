package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.skills;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Skills {
    private Skill acrobatics = new Skill();
    private Skill sleight_of_hand = new Skill();
    private Skill stealth = new Skill();

    private Skill athletics = new Skill();

    private Skill arcana = new Skill();
    private Skill history = new Skill();
    private Skill investigation = new Skill();
    private Skill nature = new Skill();
    private Skill religion = new Skill();

    private Skill animal_handling = new Skill();
    private Skill perception = new Skill();
    private Skill insight = new Skill();
    private Skill survival = new Skill();
    private Skill medicine = new Skill();

    private Skill performance = new Skill();
    private Skill deception = new Skill();
    private Skill intimidation = new Skill();
    private Skill persuasion = new Skill();
}
