package es.ujaen.ahg00048.microservice_characterSheet.rest;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import es.ujaen.ahg00048.microservice_characterSheet.exception.CharacterSheetRegistrationException;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.CharacterSheetDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.mapper.CharacterSheetMapper;
import es.ujaen.ahg00048.microservice_characterSheet.service.CharacterSheetService;

@RestController
@RequestMapping("/api/charSheets")
public class CharacterSheetController {
    @Autowired
    private CharacterSheetService _service;

    @Autowired
    private CharacterSheetMapper _mapper;


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public void validationConstraintViolationException() {}

    @GetMapping
    public ResponseEntity<List<String>> getSheets(@RequestParam String userId) {
        return ResponseEntity.ok(_service.getCharSheets(userId));
    }

    @PostMapping
    public ResponseEntity<Void> addSheet(@RequestBody CharacterSheetDTO characterSheetDTO) {
        try {
            _service.addCharSheet(_mapper.entity(characterSheetDTO));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (CharacterSheetRegistrationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterSheetDTO> getSheet(@PathVariable(value = "id") String id) {
        try {
            return ResponseEntity.ok(_mapper.dto(_service.getCharSheet(id)));
        } catch (CharacterSheetRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharacterSheetDTO> modifySheet(@PathVariable(value = "id") String id, @RequestBody CharacterSheetDTO characterSheetDTO) {
        try {
            CharacterSheet characterSheet = _service.modifyCharSheet(id, _mapper.entity(characterSheetDTO));
            return ResponseEntity.ok(_mapper.dto(characterSheet));
        } catch (CharacterSheetRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeSheet(@PathVariable(value = "id") String id) {
        try {
            _service.removeCharSheet(id);
            return ResponseEntity.ok().build();
        } catch (CharacterSheetRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
