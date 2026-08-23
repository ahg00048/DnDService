package es.ujaen.ahg00048.microservice_lobby.service;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class LobbyService {
    private final Map<String, Lobby> _lobbiesRep = new HashMap<>();


    public Lobby createLobby(@Email String userId, boolean open, String password) {
        return null;
    }

    public Lobby joinLobby(@Email String userId, String id) {
        return null;
    }

    public void leaveLobby(@Email String userId, String id) {

    }
}
