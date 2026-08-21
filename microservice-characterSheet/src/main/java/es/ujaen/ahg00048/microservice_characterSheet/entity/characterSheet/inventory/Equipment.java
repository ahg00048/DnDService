package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {
    private String name = "";
    private String type = "";
    private String description = "";
    private float weight = 0.0f;
    private int amount = 0;
}
