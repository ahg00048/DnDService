package es.ujaen.ahg00048.microservice_characterSheet.service;

import es.ujaen.ahg00048.microservice_characterSheet.exception.InvalidOperationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
    private Environment _env;

    @Autowired
    private CharacterSheetRepository _charSheetsRep;

    public static int MAX_NUMBER_SHEETS_PER_USER;


    @Autowired
    public CharacterSheetService(@Value("${app.user.max.characterSheets}") int max_number_sheets_per_user) {
        MAX_NUMBER_SHEETS_PER_USER = max_number_sheets_per_user;
    }


    public List<CharacterSheet> getCharSheets(@Email @NotBlank String userId) {
        return _charSheetsRep.findAllByUserId(userId);
    }

    public CharacterSheet addCharSheet(@Email @NotBlank String userId, @Valid CharacterSheet charSheet)
            throws InvalidOperationException {
        charSheet.setUserId(userId);

        if (_charSheetsRep.countAllByUserId(charSheet.getUserId()) >= MAX_NUMBER_SHEETS_PER_USER)
            throw new InvalidOperationException();

        return _charSheetsRep.insert(charSheet);
    }

    public CharacterSheet modifyCharSheet(@Email @NotBlank String userId, @NotBlank String id, @Valid CharacterSheet charSheet)
            throws CharacterSheetRegistrationException, InvalidOperationException {
        CharacterSheet savedCharSheet = _charSheetsRep.findById(id).orElseThrow(CharacterSheetRegistrationException::new);

        if (!savedCharSheet.getUserId().equals(userId))
            throw new InvalidOperationException();

        charSheet.setUserId(userId);
        charSheet.setId(id);

        return _charSheetsRep.save(charSheet);
    }

    public void removeCharSheet(@Email @NotBlank String userId, @NotBlank String id)
            throws CharacterSheetRegistrationException, InvalidOperationException {
        CharacterSheet savedCharSheet = _charSheetsRep.findById(id).orElseThrow(CharacterSheetRegistrationException::new);

        if (!savedCharSheet.getUserId().equals(userId))
            throw new InvalidOperationException();

        _charSheetsRep.deleteById(id);
    }
}
