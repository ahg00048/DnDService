package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PassiveEffect {
    private String effect  = "";
    private String source = "";
}
