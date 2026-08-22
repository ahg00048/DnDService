package es.ujaen.ahg00048.microservice_characterSheet.rest.mapper;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.inventory.Equipment;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.inventory.Inventory;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.inventory.Weapon;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.Conditions;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.Feature;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.PassiveEffect;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.Spell;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.attributes.Attribute;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.attributes.Attributes;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.saves.Save;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.saves.Saves;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.skills.Skill;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.skills.Skills;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.CharacterSheetDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.inventory.InventoryDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.ConditionsDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.attributes.AttributesDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.saves.SavesDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties.skills.SkillsDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacterSheetMapper {
    private Skills entitySkills(CharacterSheetDTO characterSheetDTO) {
        SkillsDTO skillsDTO = characterSheetDTO.skills();
        return new Skills(new Skill(skillsDTO.acrobatics().proficient(), skillsDTO.acrobatics().value(), skillsDTO.acrobatics().modifier()),
                new Skill(skillsDTO.sleight_of_hand().proficient(), skillsDTO.sleight_of_hand().value(), skillsDTO.sleight_of_hand().modifier()),
                new Skill(skillsDTO.stealth().proficient(), skillsDTO.stealth().value(), skillsDTO.stealth().modifier()),

                new Skill(skillsDTO.athletics().proficient(), skillsDTO.athletics().value(), skillsDTO.athletics().modifier()),

                new Skill(skillsDTO.arcana().proficient(), skillsDTO.arcana().value(), skillsDTO.arcana().modifier()),
                new Skill(skillsDTO.history().proficient(), skillsDTO.history().value(), skillsDTO.history().modifier()),
                new Skill(skillsDTO.investigation().proficient(), skillsDTO.investigation().value(), skillsDTO.investigation().modifier()),
                new Skill(skillsDTO.nature().proficient(), skillsDTO.nature().value(), skillsDTO.nature().modifier()),
                new Skill(skillsDTO.religion().proficient(), skillsDTO.religion().value(), skillsDTO.religion().modifier()),

                new Skill(skillsDTO.animal_handling().proficient(), skillsDTO.animal_handling().value(), skillsDTO.animal_handling().modifier()),
                new Skill(skillsDTO.perception().proficient(), skillsDTO.perception().value(), skillsDTO.perception().modifier()),
                new Skill(skillsDTO.insight().proficient(), skillsDTO.insight().value(), skillsDTO.insight().modifier()),
                new Skill(skillsDTO.survival().proficient(), skillsDTO.survival().value(), skillsDTO.survival().modifier()),
                new Skill(skillsDTO.medicine().proficient(), skillsDTO.medicine().value(), skillsDTO.medicine().modifier()),

                new Skill(skillsDTO.performance().proficient(), skillsDTO.performance().value(), skillsDTO.performance().modifier()),
                new Skill(skillsDTO.deception().proficient(), skillsDTO.deception().value(), skillsDTO.deception().modifier()),
                new Skill(skillsDTO.intimidation().proficient(), skillsDTO.intimidation().value(), skillsDTO.intimidation().modifier()),
                new Skill(skillsDTO.persuasion().proficient(), skillsDTO.persuasion().value(), skillsDTO.persuasion().modifier()));
    }

    private Saves entitySaves(CharacterSheetDTO characterSheetDTO) {
        SavesDTO savesDTO = characterSheetDTO.saves();
        return new Saves(new Save(savesDTO.strength().proficient(), savesDTO.strength().modifier()),
                new Save(savesDTO.dexterity().proficient(), savesDTO.dexterity().modifier()),
                new Save(savesDTO.constitution().proficient(), savesDTO.constitution().modifier()),
                new Save(savesDTO.intelligence().proficient(), savesDTO.intelligence().modifier()),
                new Save(savesDTO.wisdom().proficient(), savesDTO.wisdom().modifier()),
                new Save(savesDTO.charisma().proficient(), savesDTO.charisma().modifier()));
    }

    private Attributes entityAttributes(CharacterSheetDTO characterSheetDTO) {
        AttributesDTO attributesDTO = characterSheetDTO.attributes();
        return new Attributes(new Attribute(attributesDTO.strength().value(), attributesDTO.strength().modifier()),
                new Attribute(attributesDTO.dexterity().value(), attributesDTO.dexterity().modifier()),
                new Attribute(attributesDTO.constitution().value(), attributesDTO.constitution().modifier()),
                new Attribute(attributesDTO.intelligence().value(), attributesDTO.intelligence().modifier()),
                new Attribute(attributesDTO.wisdom().value(), attributesDTO.wisdom().modifier()),
                new Attribute(attributesDTO.charisma().value(), attributesDTO.charisma().modifier()));
    }

    private Inventory entityInventory(CharacterSheetDTO characterSheetDTO) {
        InventoryDTO inventoryDTO = characterSheetDTO.inventory();
        return new Inventory(inventoryDTO.weapons()
                .stream().map(wDTO -> new Weapon(wDTO.name(), wDTO.description(), wDTO.rollType(), wDTO.roll(), wDTO.weight())).toList(),
                inventoryDTO.equipment()
                .stream().map(eDTO -> new Equipment(eDTO.name(), eDTO.type(), eDTO.description(), eDTO.weight(), eDTO.amount())).toList(),
                inventoryDTO.gold(), inventoryDTO.weight(), inventoryDTO.maxWeight());
    }

    private Conditions entityConditions(CharacterSheetDTO characterSheetDTO) {
        ConditionsDTO conditionsDTO = characterSheetDTO.conditions();
        return new Conditions(conditionsDTO.grappled(), conditionsDTO.restrained(), conditionsDTO.frightened(), conditionsDTO.stunned(),
                conditionsDTO.exhaustion(), conditionsDTO.blinded(), conditionsDTO.prone(), conditionsDTO.deafened(), conditionsDTO.poisoned(),
                conditionsDTO.charmed(), conditionsDTO.incapacitated(), conditionsDTO.unconscious(), conditionsDTO.invisible(), conditionsDTO.petrified(),
                conditionsDTO.paralyzed());
    }

    public CharacterSheet entity(CharacterSheetDTO characterSheetDTO) {
        Attributes attributes = entityAttributes(characterSheetDTO);

        Saves saves = entitySaves(characterSheetDTO);

        Skills skills = entitySkills(characterSheetDTO);

        Inventory inventory = entityInventory(characterSheetDTO);

        Conditions conditions = entityConditions(characterSheetDTO);

        List<Spell> spells = characterSheetDTO.spells()
                .stream().map(sDTO -> new Spell(sDTO.type(), sDTO.name(), sDTO.description(),
                                                sDTO.range(), sDTO.target(), sDTO.components(),
                                                sDTO.castTime(), sDTO.duration(), sDTO.roll())).toList();

        List<PassiveEffect> passiveEffects = characterSheetDTO.passiveEffects()
                .stream().map(peDTO -> new PassiveEffect(peDTO.effect(), peDTO.source())).toList();

        List<Feature> features = characterSheetDTO.features()
                .stream().map(fDTO -> new Feature(fDTO.name(), fDTO.description())).toList();

        return new CharacterSheet(characterSheetDTO.id(), characterSheetDTO.imageId(), characterSheetDTO.userId(), characterSheetDTO.name(), characterSheetDTO.classname(), characterSheetDTO.level(),
                characterSheetDTO.defense(), characterSheetDTO.initiative(), characterSheetDTO.proficiency(), characterSheetDTO.speed(), characterSheetDTO.healthPoints(),
                characterSheetDTO.maxHealthPoints(), characterSheetDTO.hitRolls(), characterSheetDTO.maxHitRolls(), attributes, saves, skills, inventory, conditions,
                spells, passiveEffects, features, characterSheetDTO.biography());
    }

    public CharacterSheetDTO dto(CharacterSheet characterSheet) {
        return new CharacterSheetDTO(characterSheet);
    }
}
