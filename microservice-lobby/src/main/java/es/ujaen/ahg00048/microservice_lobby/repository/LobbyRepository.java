package es.ujaen.ahg00048.microservice_lobby.repository;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LobbyRepository extends CrudRepository<String, Lobby> {

}
