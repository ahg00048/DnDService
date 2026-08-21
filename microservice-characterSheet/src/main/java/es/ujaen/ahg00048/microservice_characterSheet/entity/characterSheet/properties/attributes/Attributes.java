package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.attributes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Attributes {
    private Attribute strength;
    private Attribute dexterity;
    private Attribute constitution;
    private Attribute intelligence;
    private Attribute wisdom;
    private Attribute charisma;
}
