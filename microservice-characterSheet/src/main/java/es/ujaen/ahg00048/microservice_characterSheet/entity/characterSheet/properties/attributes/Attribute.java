package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.attributes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Attribute {
    private int value = 10;
    private int modifier = 0;
}
