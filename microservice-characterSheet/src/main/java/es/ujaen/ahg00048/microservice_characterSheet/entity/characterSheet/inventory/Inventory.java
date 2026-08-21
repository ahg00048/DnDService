package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.inventory;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Inventory {
    private final List<Weapon> weapons = new ArrayList<>();
    private final List<Equipment> equipment = new ArrayList<>();
    private float gold = 0.0f;
    private float weight = 0.0f;
    private float maxWeight = 50.0f;
}
