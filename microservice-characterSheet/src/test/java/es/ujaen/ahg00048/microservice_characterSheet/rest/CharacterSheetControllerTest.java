package es.ujaen.ahg00048.microservice_characterSheet.rest;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.CharacterSheetDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.mapper.CharacterSheetMapper;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

@SpringBootTest(classes = es.ujaen.ahg00048.microservice_characterSheet.app.MicroserviceCharacterSheetApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class RestControllerTest {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private RestTestClient _restClient;

    @Autowired
    private MongoTemplate _mongoTemplate;

    @Autowired
    private CharacterSheetMapper _mapper;


    @PostConstruct
    @AfterEach
    public void cleanUp() {
        _mongoTemplate.getDb().drop();
    }

    @Test
    @DirtiesContext
    public void getSheet() {
        String randomEmail = "random@gmail.com"; // registered user

        CharacterSheet characterSheet = new CharacterSheet();
        characterSheet.setUserId(randomEmail);
        characterSheet.setLevel(1);
        characterSheet.setName("some name");
        characterSheet.setClassname("someClass");

        List<CharacterSheetDTO> sheets = _restClient.get() // empty list due to unregistered sheets
                .uri("/api/v1/charSheets?userId=" + randomEmail)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CharacterSheetDTO>>() {
                })
                .returnResult().getResponseBody();

        Assertions.assertEquals(0, sheets.size());

        for(int i = 0; i < 2; i++) { // add two sheets
            _restClient.post()
                    .uri("/api/v1/charSheets")
                    .body(_mapper.dto(characterSheet))
                    .exchange()
                    .expectStatus().isCreated();
        }

        sheets = _restClient.get() // list of size 2
                .uri("/api/v1/charSheets?userId=" + randomEmail)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CharacterSheetDTO>>() {})
                .returnResult().getResponseBody();

        Assertions.assertEquals(2, sheets.size());
    }

    @Test
    @DirtiesContext
    public void addSheets() {
        String randomEmail = "random@gmail.com"; // registered user

        CharacterSheet characterSheet = new CharacterSheet();
        characterSheet.setUserId(randomEmail);
        characterSheet.setLevel(1);
        characterSheet.setName("some name");
        characterSheet.setClassname("someClass");

        Integer maxSheetsAllowed = _restClient.get()
                .uri("/api/v1/charSheets/maxAllowed")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .returnResult().getResponseBody();


        for(int i = 0; i < maxSheetsAllowed; i++) {
            _restClient.post() // add sheet
                    .uri("/api/v1/charSheets")
                    .body(_mapper.dto(characterSheet))
                    .exchange()
                    .expectStatus().isCreated();
        }

        _restClient.post() // add more than the limit
                .uri("/api/v1/charSheets")
                .body(_mapper.dto(characterSheet))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DirtiesContext
    public void modifySheet() {
        String randomEmail = "random@gmail.com"; // registered user

        CharacterSheet characterSheet = new CharacterSheet();
        characterSheet.setUserId(randomEmail);
        characterSheet.setLevel(1);
        characterSheet.setName("some name");
        characterSheet.setClassname("someClass");

        _restClient.post() // add sheet
                .uri("/api/v1/charSheets")
                .body(_mapper.dto(characterSheet))
                .exchange()
                .expectStatus().isCreated();

        List<CharacterSheetDTO> sheets = _restClient.get() // list of size 2
                .uri("/api/v1/charSheets?userId=" + randomEmail)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CharacterSheetDTO>>() {})
                .returnResult().getResponseBody();

        CharacterSheetDTO characterSheetDTO = sheets.getFirst();

        Assertions.assertEquals(1, characterSheetDTO.level());

        characterSheet = _mapper.entity(characterSheetDTO);
        characterSheet.setLevel(2);
        characterSheetDTO = _mapper.dto(characterSheet);

        _restClient.put()
                .uri("/api/v1/charSheets/invalid_id")
                .body(characterSheetDTO)
                .exchange()
                .expectStatus().isNotFound();

        characterSheetDTO = _restClient.put()
                .uri("/api/v1/charSheets/" + sheets.getLast().id())
                .body(characterSheetDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CharacterSheetDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertEquals(2, characterSheetDTO.level());
    }

    @Test
    @DirtiesContext
    public void removeSheet() {
        String randomEmail = "random@gmail.com"; // registered user

        CharacterSheet characterSheet = new CharacterSheet();
        characterSheet.setUserId(randomEmail);
        characterSheet.setLevel(1);
        characterSheet.setName("some name");
        characterSheet.setClassname("someClass");

        _restClient.post() // add sheet
                .uri("/api/v1/charSheets")
                .body(_mapper.dto(characterSheet))
                .exchange()
                .expectStatus().isCreated();

        List<CharacterSheetDTO> sheets = _restClient.get() // list of size 1
                .uri("/api/v1/charSheets?userId=" + randomEmail)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CharacterSheetDTO>>() {})
                .returnResult().getResponseBody();

        CharacterSheetDTO characterSheetDTO = sheets.getFirst();
        _restClient.delete() // Removing unregistered sheet
                .uri("/api/v1/charSheets/invalid_id")
                .exchange().
                expectStatus().isNotFound();

        _restClient.delete() // Removing sheet
                .uri("/api/v1/charSheets/" + characterSheetDTO.id())
                .exchange().
                expectStatus().isOk();

        sheets = _restClient.get() // list of size 0
                .uri("/api/v1/charSheets?userId=" + randomEmail)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CharacterSheetDTO>>() {})
                .returnResult().getResponseBody();

        Assertions.assertEquals(0, sheets.size());
    }
}