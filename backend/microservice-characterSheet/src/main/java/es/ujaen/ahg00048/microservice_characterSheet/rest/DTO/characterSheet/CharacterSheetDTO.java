package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet;

import java.util.ArrayList;
import java.util.List;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;

import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.inventory.InventoryDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.ConditionsDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.FeatureDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.PassiveEffectDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.SpellDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.attributes.AttributesDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.saves.SavesDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.skills.SkillsDTO;


public record CharacterSheetDTO(String id, String imageId, String userId, String name, String classname, int level,
                                int defense, int initiative, int proficiency, int speed, int healthPoints, int maxHealthPoints,
                                int hitRolls, int maxHitRolls, AttributesDTO attributes, SavesDTO saves, SkillsDTO skills, InventoryDTO inventory,
                                ConditionsDTO conditions, List<SpellDTO> spells, List<PassiveEffectDTO> passiveEffects, List<FeatureDTO> features, String biography) {
    public CharacterSheetDTO(CharacterSheet characterSheet) {
        this(characterSheet.getId(), characterSheet.getImageId(), characterSheet.getUserId(), characterSheet.getName(), characterSheet.getClassname(), characterSheet.getLevel(),
                characterSheet.getDefense(), characterSheet.getInitiative(), characterSheet.getProficiency(), characterSheet.getSpeed(), characterSheet.getHealthPoints(), characterSheet.getMaxHealthPoints(),
                characterSheet.getHitRolls(), characterSheet.getMaxHitRolls(), new AttributesDTO(characterSheet.getAttributes()), new SavesDTO(characterSheet.getSaves()), new SkillsDTO(characterSheet.getSkills()),
                new InventoryDTO(characterSheet.getInventory()), new ConditionsDTO(characterSheet.getConditions()), (new ArrayList<>(characterSheet.getSpells())).stream().map(SpellDTO::new).toList(),
                (new ArrayList<>(characterSheet.getPassiveEffects())).stream().map(PassiveEffectDTO::new).toList(), (new ArrayList<>(characterSheet.getFeatures())).stream().map(FeatureDTO::new).toList(),
                characterSheet.getBiography());
    }
}
