package es.ujaen.ahg00048.microservice_characterSheet.rest;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;
import es.ujaen.ahg00048.microservice_characterSheet.rest.DTO.characterSheet.CharacterSheetDTO;
import es.ujaen.ahg00048.microservice_characterSheet.rest.mapper.CharacterSheetMapper;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = es.ujaen.ahg00048.microservice_characterSheet.app.MicroserviceCharacterSheetApplication.class)
@ContextConfiguration
@WebAppConfiguration
public class CharacterSheetControllerTest {
        @Autowired
        private WebApplicationContext context;

        private MockMvc _mvc;

        @Autowired
        private ObjectMapper _oMapper;

        @Autowired
        private MongoTemplate _mongoTemplate;

        @Autowired
        private CharacterSheetMapper _mapper;

        @PostConstruct
        @AfterEach
        public void cleanUp() {
                _mongoTemplate.getDb().drop();
        }

        @BeforeEach
        public void setup() {
                _mvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(SecurityMockMvcConfigurers.springSecurity())
                                .build();
        }

        @Test
        @DirtiesContext
        public void getSheet() throws Exception {
                String randomUserId = "random-id"; // registered user

                CharacterSheet characterSheet = new CharacterSheet();
                characterSheet.setLevel(1);
                characterSheet.setName("some name");
                characterSheet.setClassname("someClass");

                MvcResult result = _mvc.perform(
                                get("/api/v1/charSheets")
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId)))))
                                .andExpect(status().isOk())
                                .andReturn();

                List<CharacterSheetDTO> sheets = _oMapper.readValue(result.getResponse().getContentAsString(),
                                new TypeReference<>() {
                                });

                Assertions.assertEquals(0, sheets.size());

                for (int i = 0; i < 2; i++) { // add two sheets
                        _mvc.perform(
                                        post("/api/v1/charSheets")
                                                        .with(jwt().jwt((jwt -> jwt.subject(randomUserId))))
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .content(_oMapper.writeValueAsString(
                                                                        _mapper.dto(characterSheet))))
                                        .andExpect(status().isCreated());
                }

                result = _mvc.perform(
                                get("/api/v1/charSheets")
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId)))))
                                .andExpect(status().isOk())
                                .andReturn();

                sheets = _oMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertEquals(2, sheets.size());
        }

        @Test
        @DirtiesContext
        public void addSheets() throws Exception {
                String randomUserId = "random-id"; // registered user

                CharacterSheet characterSheet = new CharacterSheet();
                characterSheet.setLevel(1);
                characterSheet.setName("some name");
                characterSheet.setClassname("someClass");

                MvcResult result = _mvc.perform(
                                get("/api/v1/charSheets/maxAllowed")
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId)))))
                                .andExpect(status().isOk())
                                .andReturn();

                Integer maxSheetsAllowed = _oMapper.readValue(result.getResponse().getContentAsString(), Integer.class);

                for (int i = 0; i < maxSheetsAllowed; i++) {
                        _mvc.perform(
                                        post("/api/v1/charSheets")
                                                        .with(jwt().jwt((jwt -> jwt.subject(randomUserId))))
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .content(_oMapper.writeValueAsString(
                                                                        _mapper.dto(characterSheet))))
                                        .andExpect(status().isCreated());
                }

                _mvc.perform(
                                post("/api/v1/charSheets")
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId))))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(_oMapper.writeValueAsString(_mapper.dto(characterSheet))))
                                .andExpect(status().isConflict());
        }

        @Test
        @DirtiesContext
        public void modifySheet() throws Exception {
                String randomUserId = "random-id"; // registered user

                CharacterSheet characterSheet = new CharacterSheet();
                characterSheet.setLevel(1);
                characterSheet.setName("some name");
                characterSheet.setClassname("someClass");

                MvcResult result = _mvc.perform(
                                post("/api/v1/charSheets")
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId))))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(_oMapper.writeValueAsString(_mapper.dto(characterSheet))))
                                .andExpect(status().isCreated())
                                .andReturn();

                result = _mvc.perform(
                                get("/api/v1/charSheets")
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId)))))
                                .andExpect(status().isOk())
                                .andReturn();

                List<CharacterSheetDTO> sheets = _oMapper.readValue(result.getResponse().getContentAsString(),
                                new TypeReference<>() {
                                });

                CharacterSheetDTO characterSheetDTO = sheets.getFirst();

                Assertions.assertEquals(1, characterSheetDTO.level());

                characterSheet = _mapper.entity(characterSheetDTO);
                characterSheet.setLevel(2);
                characterSheetDTO = _mapper.dto(characterSheet);

                _mvc.perform(
                                put("/api/v1/charSheets/invalid_id")
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId))))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(_oMapper.writeValueAsString(characterSheetDTO)))
                                .andExpect(status().isNotFound());

                result = _mvc.perform(
                                put("/api/v1/charSheets/" + characterSheet.getId())
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId))))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(_oMapper.writeValueAsString(characterSheetDTO)))
                                .andExpect(status().isOk())
                                .andReturn();

                characterSheetDTO = _oMapper.readValue(result.getResponse().getContentAsString(),
                                CharacterSheetDTO.class);

                Assertions.assertEquals(2, characterSheetDTO.level());
        }

        @Test
        @DirtiesContext
        public void removeSheet() throws Exception {
                String randomUserId = "random-id"; // registered user

                CharacterSheet characterSheet = new CharacterSheet();
                characterSheet.setLevel(1);
                characterSheet.setName("some name");
                characterSheet.setClassname("someClass");

                _mvc.perform(
                                post("/api/v1/charSheets")
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId))))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(_oMapper.writeValueAsString(_mapper.dto(characterSheet))))
                                .andExpect(status().isCreated());

                MvcResult result = _mvc.perform(
                                get("/api/v1/charSheets")
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId)))))
                                .andExpect(status().isOk())
                                .andReturn();

                List<CharacterSheetDTO> sheets = _oMapper.readValue(result.getResponse().getContentAsString(),
                                new TypeReference<>() {
                                });

                CharacterSheetDTO characterSheetDTO = sheets.getFirst();

                _mvc.perform( // Removing unregistered sheet
                                delete("/api/v1/charSheets/invalid_id")
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId))))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(_oMapper.writeValueAsString(_mapper.dto(characterSheet))))
                                .andExpect(status().isNotFound());

                _mvc.perform( // Removing unregistered sheet
                                delete("/api/v1/charSheets/" + characterSheetDTO.id())
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId))))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(_oMapper.writeValueAsString(_mapper.dto(characterSheet))))
                                .andExpect(status().isOk());

                result = _mvc.perform(
                                get("/api/v1/charSheets")
                                                .with(jwt().jwt((jwt -> jwt.subject(randomUserId)))))
                                .andExpect(status().isOk())
                                .andReturn();

                sheets = _oMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertEquals(0, sheets.size());
        }
}