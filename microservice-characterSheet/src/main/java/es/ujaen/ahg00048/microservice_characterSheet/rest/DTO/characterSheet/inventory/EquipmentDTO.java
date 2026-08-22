package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.inventory;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.inventory.Equipment;

public record EquipmentDTO(String name, String type, String description, float weight, int amount) {
    public EquipmentDTO(Equipment equipment) {
        this(equipment.getName(), equipment.getType(), equipment.getDescription(), equipment.getWeight(), equipment.getAmount());
    }
}
