package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties;

import es.ujaen.ahg00048.microservice_characterSheet.qualifier.DiceRoll;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Spell {
    private String type = "";
    private String name = "";
    private String description = "";
    private int range = 0;
    private String target = "";
    private String components = "";
    private String castTime = "";
    private String duration = "";
    private String roll = "";
}
