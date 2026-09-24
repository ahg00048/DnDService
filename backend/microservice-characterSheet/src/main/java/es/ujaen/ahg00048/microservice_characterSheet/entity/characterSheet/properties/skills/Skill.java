package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.skills;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Skill {
    private boolean proficient = false;
    private int value = 5;
    private int modifier = 0;
}
