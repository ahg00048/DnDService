package es.ujaen.ahg00048.microservice_characterSheet.service;

import com.mongodb.BasicDBObject;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import es.ujaen.ahg00048.microservice_characterSheet.repository.CharacterSheetRepository;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;
import es.ujaen.ahg00048.microservice_characterSheet.exception.CharacterSheetRegistrationException;

@Service
@Validated
@NoArgsConstructor
public class CharacterSheetService {
    @Autowired
    private CharacterSheetRepository _charSheetsRep;

    private final static int max_number_sheets_por_user = 10;


    public int getMaxCharSheetsAllowed() {
        return max_number_sheets_por_user;
    }

    public List<String> getCharSheets(@Email @NotBlank String userId) {
        return _charSheetsRep.findAllByUserId(userId).stream().map(CharacterSheet::getId).toList();
    }

    public CharacterSheet getCharSheet(@NotBlank String id) throws CharacterSheetRegistrationException {
        return _charSheetsRep.findById(id).orElseThrow(CharacterSheetRegistrationException::new);
    }

    public void addCharSheet(@Valid CharacterSheet charSheet) throws CharacterSheetRegistrationException {
        if (_charSheetsRep.countAllByUserId(charSheet.getUserId()) >= max_number_sheets_por_user)
            throw new CharacterSheetRegistrationException();

        _charSheetsRep.insert(charSheet);
    }

    public CharacterSheet modifyCharSheet(@NotBlank String id, @Valid CharacterSheet charSheet) throws CharacterSheetRegistrationException {
        if (!_charSheetsRep.existsById(id))
            throw new CharacterSheetRegistrationException();

        charSheet.setId(id); // Permitimos asi no solo modificacion de la pag. actual, sino copias de una a otra

        return _charSheetsRep.save(charSheet);
    }

    public void removeCharSheet(@NotBlank String id) throws CharacterSheetRegistrationException {
        if (!_charSheetsRep.existsById(id))
            throw new CharacterSheetRegistrationException();

        _charSheetsRep.deleteById(id);
    }
}
