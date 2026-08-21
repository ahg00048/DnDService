package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.saves;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Saves {
    private final Save strength = new Save();
    private final Save dexterity = new Save();
    private final Save constitution = new Save();
    private final Save intelligence = new Save();
    private final Save wisdom = new Save();
    private final Save charisma = new Save();
}
