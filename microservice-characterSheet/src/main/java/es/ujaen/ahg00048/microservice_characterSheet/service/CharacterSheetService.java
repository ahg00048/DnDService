package es.ujaen.ahg00048.microservice_characterSheet.service;

import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;
import es.ujaen.ahg00048.microservice_characterSheet.exception.CharacterSheetRegistrationException;

@Service
@Validated
@NoArgsConstructor
public class CharacterSheetService {
    private final Map<String, CharacterSheet> _charSheets = new HashMap<>();

    public List<String> getCharSheets(String userId) throws CharacterSheetRegistrationException {
        List<String> userCharSheets = new ArrayList<>();

        for (CharacterSheet charSheet : _charSheets.values()) {
            if (charSheet.getUserId().equals(userId))
                userCharSheets.add(charSheet.getId());
        }

        return userCharSheets;
    }

    public CharacterSheet getCharSheet(String id) throws CharacterSheetRegistrationException {
        if (!_charSheets.containsKey(id))
            throw new CharacterSheetRegistrationException();

        return _charSheets.get(id);
    }

    public void addCharSheet(@Valid CharacterSheet charSheet) throws CharacterSheetRegistrationException {
        _charSheets.put(charSheet.getId(), charSheet);
    }

    public CharacterSheet modifyCharSheet(@Valid CharacterSheet charSheet) throws CharacterSheetRegistrationException {
        if (!_charSheets.containsKey(charSheet.getId()))
            throw new CharacterSheetRegistrationException();

        return _charSheets.put(charSheet.getId(), charSheet);
    }

    public void removeCharSheet(String id) throws CharacterSheetRegistrationException {
        if (!_charSheets.containsKey(id))
            throw new CharacterSheetRegistrationException();

        _charSheets.remove(id);
    }
}
