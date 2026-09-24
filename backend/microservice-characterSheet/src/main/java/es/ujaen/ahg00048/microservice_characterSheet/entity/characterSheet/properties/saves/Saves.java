package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.saves;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Saves {
    private Save strength = new Save();
    private Save dexterity = new Save();
    private Save constitution = new Save();
    private Save intelligence = new Save();
    private Save wisdom = new Save();
    private Save charisma = new Save();
}
