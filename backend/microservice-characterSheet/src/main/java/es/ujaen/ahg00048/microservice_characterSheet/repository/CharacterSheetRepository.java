package es.ujaen.ahg00048.microservice_characterSheet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
public interface CharacterSheetRepository extends MongoRepository<CharacterSheet, String> {
    List<CharacterSheet> findAllByUserId(String userId);
    List<CharacterSheet> findAllByImageId(String imageId);
    void deleteAllByUserId(String userId);
    int countAllByUserId(String userId);
}
