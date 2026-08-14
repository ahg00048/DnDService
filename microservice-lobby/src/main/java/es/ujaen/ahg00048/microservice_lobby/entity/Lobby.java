package es.ujaen.ahg00048.microservice_lobby.entity;

import es.ujaen.ahg00048.microservice_lobby.entity.boardGame.Board;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Lobby {
    private String id;
    private boolean open;       // public/private lobby
    private String password;

    private final List<String> users;
    private Board board;


    public Lobby(String lobbyCreator, boolean open, String password) {
        users = new ArrayList<>();
        users.add(lobbyCreator);

        this.open = open;
        this.password = password;
    }
}
