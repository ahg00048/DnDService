package es.ujaen.ahg00048.microservice_characterSheet.service;

import es.ujaen.ahg00048.microservice_characterSheet.exception.InvalidOperationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import es.ujaen.ahg00048.microservice_characterSheet.repository.CharacterSheetRepository;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;
import es.ujaen.ahg00048.microservice_characterSheet.exception.CharacterSheetRegistrationException;


@Service
@Validated
public class CharacterSheetService {
    @Autowired
    private CharacterSheetRepository _charSheetsRep;


    public static int MAX_NUMBER_SHEETS_PER_USER;


    @Autowired
    public CharacterSheetService(@Value("${app.user.max.characterSheets}") int max_number_sheets_per_user) {
        MAX_NUMBER_SHEETS_PER_USER = max_number_sheets_per_user;
    }


    public List<CharacterSheet> getCharSheets(@NotBlank String userId) {
        return _charSheetsRep.findAllByUserId(userId);
    }


    public CharacterSheet addCharSheet(@NotBlank String userId, @Valid CharacterSheet charSheet)
            throws InvalidOperationException {
        charSheet.setUserId(userId);

        if (_charSheetsRep.countAllByUserId(charSheet.getUserId()) >= MAX_NUMBER_SHEETS_PER_USER)
            throw new InvalidOperationException();

        return _charSheetsRep.insert(charSheet);
    }

    public CharacterSheet modifyCharSheet(@NotBlank String userId, @NotBlank String id, @Valid CharacterSheet charSheet)
            throws CharacterSheetRegistrationException, InvalidOperationException {
        CharacterSheet savedCharSheet = _charSheetsRep.findById(id).orElseThrow(CharacterSheetRegistrationException::new);

        if (!savedCharSheet.getUserId().equals(userId))
            throw new InvalidOperationException();

        charSheet.setUserId(userId);
        charSheet.setId(id);
        charSheet.setVersion(savedCharSheet.getVersion());

        return _charSheetsRep.save(charSheet);
    }

    public void removeCharSheet(@NotBlank String userId, @NotBlank String id)
            throws CharacterSheetRegistrationException, InvalidOperationException {
        CharacterSheet savedCharSheet = _charSheetsRep.findById(id).orElseThrow(CharacterSheetRegistrationException::new);

        if (!savedCharSheet.getUserId().equals(userId))
            throw new InvalidOperationException();

        _charSheetsRep.deleteById(id);
    }

    /**
     * Only used by RabbitMQ listener
     */
    public void removeImage_admin(@NotBlank String imageId) {
        List<CharacterSheet> charSheets = _charSheetsRep.findAllByImageId(imageId);

        for (CharacterSheet charSheet : charSheets) {
            charSheet.setImageId("");
        }

        _charSheetsRep.saveAll(charSheets);
    }

    /**
     * Only used by RabbitMQ listener
     */
    public void removeCharSheet_admin(@NotBlank String userId) {
        _charSheetsRep.deleteAllByUserId(userId);
    }
}
