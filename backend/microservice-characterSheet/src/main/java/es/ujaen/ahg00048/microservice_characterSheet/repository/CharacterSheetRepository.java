package es.ujaen.ahg00048.microservice_characterSheet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;

import java.util.List;


public interface CharacterSheetRepository extends MongoRepository<CharacterSheet, String> {
    List<CharacterSheet> findAllByUserId(String userId);
    int countAllByUserId(String userId);
}
