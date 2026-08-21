package es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.properties;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.Conditions;

public record ConditionsDTO(boolean grappled, boolean restrained, boolean frightened, boolean stunned, boolean exhaustion, boolean blinded, boolean prone,
                            boolean deafened, boolean poisoned, boolean charmed, boolean incapacitated, boolean unconscious, boolean invisible, boolean petrified, boolean paralyzed) {
    public ConditionsDTO(Conditions conditions) {
        this(conditions.isGrappled(), conditions.isRestrained(), conditions.isFrightened(), conditions.isStunned(), conditions.isExhaustion(), conditions.isBlinded(),
                conditions.isProne(), conditions.isDeafened(), conditions.isPoisoned(), conditions.isCharmed(), conditions.isIncapacitated(), conditions.isUnconscious(),
                conditions.isInvisible(), conditions.isPetrified(), conditions.isParalyzed());
    }
}
