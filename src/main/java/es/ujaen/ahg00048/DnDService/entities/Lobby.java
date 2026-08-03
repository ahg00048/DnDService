package es.ujaen.ahg00048.DnDService.entities;

import es.ujaen.ahg00048.DnDService.entities.boardGame.Board;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private long id;
    private final List<User> users;
    private Board board;

    private boolean open;       // public/private lobby
    private String password;


    public Lobby(User lobbyCreator, boolean open, String password) {
        users = new ArrayList<>();
        users.add(lobbyCreator);

        this.open = open;
        this.password = password;
    }


    public long getId() { return id; }
    public List<User> getUsers() { return users; }
    public boolean isOpen() { return open; }
    public String getPassword() { return password; }

    public void setOpen(boolean open) { this.open = open; }
    public void setPassword(String password) { this.password = password; }
}
