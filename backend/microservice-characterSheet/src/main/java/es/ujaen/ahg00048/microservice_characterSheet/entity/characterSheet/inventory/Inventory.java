package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    private List<Weapon> weapons = new ArrayList<>();
    private List<Equipment> equipment = new ArrayList<>();
    private float gold = 0.0f;
    private float weight = 0.0f;
    private float maxWeight = 50.0f;
}
