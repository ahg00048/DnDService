package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.attributes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Attributes {
    private final Attribute strength = new Attribute();
    private final Attribute dexterity = new Attribute();
    private final Attribute constitution = new Attribute();
    private final Attribute intelligence = new Attribute();
    private final Attribute wisdom = new Attribute();
    private final Attribute charisma = new Attribute();
}
