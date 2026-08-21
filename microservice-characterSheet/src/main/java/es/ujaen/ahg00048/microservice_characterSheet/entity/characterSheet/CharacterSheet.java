package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.inventory.Inventory;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.Conditions;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.Feature;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.PassiveEffect;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.Spell;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.attributes.Attributes;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.saves.Saves;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.skills.Skills;


@Setter
@Getter
@Document("characterSheets")
public class CharacterSheet {
    @Id
    private String id;

    @NotNull
    private String imageId = "";

    @Indexed
    @NotNull @Email
    private String userId = "";
    @NotBlank
    private String name = "";
    @NotBlank
    private String classname = "";
    @Positive
    private int level = 1;

    private int defense = 0;
    private int initiative = 0;
    private int proficiency = 0;
    private int speed = 0;
    private int healthPoints = 0;
    private int maxHealthPoints = 0;
    private int hitRolls = 0;
    private int maxHitRolls = 0;

    private Attributes attributes = new Attributes();
    private Saves saves = new Saves();
    private Skills skills = new Skills();
    private Inventory inventory = new Inventory();
    private Conditions conditions = new Conditions();
    private List<Spell> spells = new ArrayList<>();
    private List<PassiveEffect> passiveEffects = new ArrayList<>();
    private List<Feature> features = new ArrayList<>();

    public String biography;

    public CharacterSheet() {
        id = new ObjectId().toString();
    }
}
