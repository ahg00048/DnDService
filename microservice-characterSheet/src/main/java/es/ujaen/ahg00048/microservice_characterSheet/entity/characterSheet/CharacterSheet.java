package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.inventory.Inventory;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.Conditions;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.Feature;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.PassiveEffect;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.Spell;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.attributes.Attributes;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.saves.Saves;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.skills.Skills;

import java.util.ArrayList;
import java.util.List;


public class CharacterSheet {
    private String id;
    private String imageId;

    public String name;
    public String classname;
    public int level = 1;

    public int defense = 0;
    public int initiative = 0;
    public int proficiency = 0;
    public int speed = 0;
    public int healthPoints = 0;
    public int maxHealthPoints = 0;
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
