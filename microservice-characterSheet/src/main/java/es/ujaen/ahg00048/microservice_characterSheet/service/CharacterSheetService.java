package es.ujaen.ahg00048.microservice_characterSheet.service;

import com.mongodb.BasicDBObject;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
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


    public List<String> getCharSheets(String userId) throws CharacterSheetRegistrationException {
        return _charSheetsRep.findAllByUserId(userId).stream().map(CharacterSheet::getId).toList();
    }

    public CharacterSheet getCharSheet(String id) throws CharacterSheetRegistrationException {
        return _charSheetsRep.findById(id).orElseThrow(CharacterSheetRegistrationException::new);
    }

    public void addCharSheet(@Valid CharacterSheet charSheet) throws CharacterSheetRegistrationException {
        _charSheetsRep.insert(charSheet);
    }

    public CharacterSheet modifyCharSheet(@Valid CharacterSheet charSheet) throws CharacterSheetRegistrationException {
        if (!_charSheetsRep.existsById(charSheet.getId()))
            throw new CharacterSheetRegistrationException();

        return _charSheetsRep.save(charSheet);
    }

    public void removeCharSheet(String id) throws CharacterSheetRegistrationException {
        if (!_charSheetsRep.existsById(id))
            throw new CharacterSheetRegistrationException();

        _charSheetsRep.deleteById(id);
    }
}
