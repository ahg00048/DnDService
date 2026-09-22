package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.inventory;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.inventory.Weapon;

public record WeaponDTO(String name, String description, String rollType, String roll, float weight) {
    public WeaponDTO(Weapon weapon) {
        this(weapon.getName(), weapon.getDescription(), weapon.getRollType(), weapon.getRoll(), weapon.getWeight());
    }
}
