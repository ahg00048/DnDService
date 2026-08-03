package es.ujaen.ahg00048.DnDService.entities.characterSheet.inventory;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    public final List<Weapon> weapons = new ArrayList<>();
    public final List<Equipment> equipment = new ArrayList<>();
    public float gold = 0.0f;
    public float weight = 0.0f;
    public float maxWeight = 50.0f;
}
