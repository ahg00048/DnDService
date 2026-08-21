package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.inventory;

import es.ujaen.ahg00048.microservice_characterSheet.qualifier.DiceRoll;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Weapon {
    private String name = "";
    private String description = "";
    private String rollType = "";
    private String roll = "";
    private float weight = 0.0f;
}
