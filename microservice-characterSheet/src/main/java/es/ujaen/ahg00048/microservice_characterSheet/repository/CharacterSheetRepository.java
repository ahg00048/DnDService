package es.ujaen.ahg00048.microservice_characterSheet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;


public interface CharacterSheetRepository extends MongoRepository<CharacterSheet, String> {
}
