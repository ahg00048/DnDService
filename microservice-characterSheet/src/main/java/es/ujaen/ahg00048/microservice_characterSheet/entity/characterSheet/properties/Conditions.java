package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Conditions {
    private boolean grappled = false;
    private boolean restrained = false;
    private boolean frightened = false;
    private boolean stunned = false;
    private boolean exhaustion = false;
    private boolean blinded = false;
    private boolean prone = false;
    private boolean deafened = false;
    private boolean poisoned = false;
    private boolean charmed = false;
    private boolean incapacitated = false;
    private boolean unconscious = false;
    private boolean invisible = false;
    private boolean petrified = false;
    private boolean paralyzed = false;
}
