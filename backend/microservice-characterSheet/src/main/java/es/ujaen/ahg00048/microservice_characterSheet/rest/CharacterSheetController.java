package es.ujaen.ahg00048.microservice_characterSheet.rest;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;
import es.ujaen.ahg00048.microservice_characterSheet.exception.InvalidOperationException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import es.ujaen.ahg00048.microservice_characterSheet.exception.CharacterSheetRegistrationException;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.CharacterSheetDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.mapper.CharacterSheetMapper;
import es.ujaen.ahg00048.microservice_characterSheet.service.CharacterSheetService;


@RestController
@RequestMapping("/api/v1/charSheets")
public class CharacterSheetController {
    @Autowired
    private CharacterSheetService _service;

    @Autowired
    private CharacterSheetMapper _mapper;


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public void validationConstraintViolationException() {}

    @GetMapping("/maxAllowed")
    public ResponseEntity<Integer> getMaxSheetsAllowed() {
        return ResponseEntity.ok(CharacterSheetService.MAX_NUMBER_SHEETS_PER_USER);
    }

    @GetMapping
    public ResponseEntity<List<CharacterSheetDTO>> getSheets(@AuthenticationPrincipal Jwt auth) {
        return ResponseEntity.ok(_service.getCharSheets(auth.getSubject()).stream().map(CharacterSheetDTO::new).toList());
    }

    @PostMapping
    public ResponseEntity<CharacterSheet> addSheet(@AuthenticationPrincipal Jwt auth,
                                                   @RequestBody CharacterSheetDTO characterSheetDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(_service.addCharSheet(auth.getSubject(), _mapper.newEntity(characterSheetDTO)));
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharacterSheetDTO> modifySheet(@AuthenticationPrincipal Jwt auth,
                                                         @PathVariable(value = "id") String id,
                                                         @RequestBody CharacterSheetDTO characterSheetDTO) {
        try {
            CharacterSheet characterSheet = _service.modifyCharSheet(auth.getSubject(), id, _mapper.entity(characterSheetDTO));
            return ResponseEntity.ok(_mapper.dto(characterSheet));
        } catch (CharacterSheetRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeSheet(@AuthenticationPrincipal Jwt auth,
                                            @PathVariable(value = "id") String id) {
        try {
            _service.removeCharSheet(auth.getSubject(), id);
            return ResponseEntity.ok().build();
        } catch (CharacterSheetRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
