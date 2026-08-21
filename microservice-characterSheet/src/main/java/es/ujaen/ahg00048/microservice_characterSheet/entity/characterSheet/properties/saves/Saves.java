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
    private Save strength;
    private Save dexterity;
    private Save constitution;
    private Save intelligence;
    private Save wisdom;
    private Save charisma;
}
