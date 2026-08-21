package es.ujaen.ahg00048.microservice_characterSheet.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/charSheets")
public class CharacterSheetController {

    public ResponseEntity<Void> get() {
        return ResponseEntity.ok().build();
    }
}
