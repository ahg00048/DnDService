package es.ujaen.ahg00048.DnDService.entities.characterSheet;

import es.ujaen.ahg00048.DnDService.entities.Image;
import es.ujaen.ahg00048.DnDService.entities.characterSheet.inventory.Inventory;
import es.ujaen.ahg00048.DnDService.entities.characterSheet.properties.Conditions;
import es.ujaen.ahg00048.DnDService.entities.characterSheet.properties.Feature;
import es.ujaen.ahg00048.DnDService.entities.characterSheet.properties.PassiveEffect;
import es.ujaen.ahg00048.DnDService.entities.characterSheet.properties.Spell;

import es.ujaen.ahg00048.DnDService.entities.characterSheet.properties.attributes.Attributes;
import es.ujaen.ahg00048.DnDService.entities.characterSheet.properties.saves.Saves;
import es.ujaen.ahg00048.DnDService.entities.characterSheet.properties.skills.Skills;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.List;

public class CharacterSheet {
    private Long id;

    public String name;
    public String classname;
    public int level = 1;

    private Image image = null;

    public int defense = 0;
    public int initiative = 0;
    public int proficiency = 0;
    public int speed = 0;
    public int healthPoints = 0;
    public int maxhealthPoints = 0;
    public int hitRolls = 0;
    public int maxHitRolls = 0;

    public Attributes attributes = new Attributes();
    public Saves saves = new Saves();
    public Skills skills = new Skills();
    public Inventory inventory = new Inventory();
    public Conditions conditions = new Conditions();
    public List<Spell> spells = new ArrayList<>();
    public List<PassiveEffect> passiveEffects = new ArrayList<>();
    public List<Feature> features = new ArrayList<>();

    public String biography;
}
