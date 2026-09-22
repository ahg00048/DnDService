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
    private Attribute strength = new Attribute();
    private Attribute dexterity = new Attribute();
    private Attribute constitution = new Attribute();
    private Attribute intelligence = new Attribute();
    private Attribute wisdom = new Attribute();
    private Attribute charisma = new Attribute();
}
