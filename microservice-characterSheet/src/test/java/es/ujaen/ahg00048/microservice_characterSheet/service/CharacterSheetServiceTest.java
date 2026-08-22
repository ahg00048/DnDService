package es.ujaen.ahg00048.microservice_characterSheet.service;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;
import es.ujaen.ahg00048.microservice_characterSheet.exception.CharacterSheetRegistrationException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest(classes = es.ujaen.ahg00048.microservice_characterSheet.app.MicroserviceCharacterSheetApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CharacterSheetServiceTest {
    @Autowired
    private CharacterSheetService _service;

    @Autowired
    private MongoTemplate _mongoTemplate;


    @PostConstruct
    @AfterEach
    public void cleanUp() {
        _mongoTemplate.getDb().drop();
    }

    @Test
    @DirtiesContext
    public void addingAndRemovingCharacterSheetTest() {
        String userId = "random@email.com";

        CharacterSheet charSheet = new CharacterSheet();

        Assertions.assertThrows(ConstraintViolationException.class, () -> _service.addCharSheet(charSheet)); // Add invalid character sheet

        charSheet.setUserId(userId);
        charSheet.setName("some char name");
        charSheet.setClassname("a classname");
        charSheet.setLevel(2);

        Assertions.assertDoesNotThrow(() -> _service.addCharSheet(charSheet)); // Add valid Character sheet

        Assertions.assertDoesNotThrow(() -> _service.removeCharSheet(charSheet.getId())); // remove character sheet

        Assertions.assertThrows(CharacterSheetRegistrationException.class, () -> _service.removeCharSheet(charSheet.getId())); // remove unregistered character sheet
    }

    @Test
    @DirtiesContext
    public void modifyCharacterSheetTest() {
        String userId = "random@email.com";

        CharacterSheet charSheet = new CharacterSheet();

        Assertions.assertThrows(ConstraintViolationException.class, () -> _service.modifyCharSheet("", charSheet)); // modify invalid character sheet

        charSheet.setUserId(userId);
        charSheet.setName("some char name");
        charSheet.setClassname("a classname");
        charSheet.setLevel(2);

        Assertions.assertThrows(CharacterSheetRegistrationException.class, () -> _service.modifyCharSheet(charSheet.getId(), charSheet)); // modify unregistered character sheet

        Assertions.assertDoesNotThrow(() -> _service.addCharSheet(charSheet)); // add valid Character sheet

        CharacterSheet respCharSheet = _service.getCharSheet(charSheet.getId()); // get character sheet

        Assertions.assertEquals(charSheet.getLevel(), respCharSheet.getLevel()); // the one saved is the same as the one delivered

        respCharSheet.setLevel(3); // Modify level

        final CharacterSheet inmutCharSheet = respCharSheet;

        Assertions.assertDoesNotThrow(() -> _service.modifyCharSheet(inmutCharSheet.getId(), inmutCharSheet)); // modify character sheet

        respCharSheet = _service.getCharSheet(charSheet.getId());

        Assertions.assertNotEquals(charSheet.getLevel(), respCharSheet.getLevel()); // the one saved is not the same as the one delivered
    }

    @Test
    @DirtiesContext
    public void getCharacterSheetTest() {
        String userId = "random@email.com";

        List<String> charSheetsIds = _service.getCharSheets(userId);

        Assertions.assertEquals(0, charSheetsIds.size()); // No character sheets saved

        int nCharSheets = 3;
        for (int i = 0; i < nCharSheets; i++) {
            CharacterSheet charSheet = new CharacterSheet();

            charSheet.setUserId(userId);
            charSheet.setName("some char name" + i);
            charSheet.setClassname("a classname");
            charSheet.setLevel(i + 1);

            Assertions.assertDoesNotThrow(() -> _service.addCharSheet(charSheet)); // add valid Character sheet
        }

        charSheetsIds = _service.getCharSheets(userId);

        Assertions.assertEquals(nCharSheets, charSheetsIds.size()); // 3 character sheets saved

        for (int i = 0; i < nCharSheets - 1; i++) {
            CharacterSheet charSheet = _service.getCharSheet(charSheetsIds.get(i));         // user gets character sheet
            CharacterSheet nextCharSheet = _service.getCharSheet(charSheetsIds.get(i + 1)); // user gets character sheet

            Assertions.assertNotEquals(nextCharSheet.getLevel(), charSheet.getLevel());     // check they are different in level
            Assertions.assertNotEquals(nextCharSheet.getName(), charSheet.getName());       // check they are different in name
        }

        Assertions.assertThrows(CharacterSheetRegistrationException.class, () -> _service.getCharSheet("invalid_id"));
    }
}
