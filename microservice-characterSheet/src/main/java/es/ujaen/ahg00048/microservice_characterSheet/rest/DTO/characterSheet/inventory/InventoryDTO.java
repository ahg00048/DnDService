package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.inventory;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public record InventoryDTO(List<WeaponDTO> weapons, List<EquipmentDTO> equipment, float gold, float weight, float maxWeight) {
    public InventoryDTO(Inventory inventory) {
        this(new ArrayList<>(inventory.getWeapons()).stream().map(WeaponDTO::new).toList(),
            new ArrayList<>(inventory.getEquipment()).stream().map(EquipmentDTO::new).toList(),
            inventory.getGold(), inventory.getWeight(), inventory.getMaxWeight());
    }
}
