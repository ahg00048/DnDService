package es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.properties.saves;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Save {
    private boolean proficient = false;
    private int modifier = 0;
}
