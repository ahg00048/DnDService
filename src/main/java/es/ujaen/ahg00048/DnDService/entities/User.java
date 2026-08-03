package es.ujaen.ahg00048.DnDService.entities;

import es.ujaen.ahg00048.DnDService.entities.characterSheet.CharacterSheet;

import java.util.ArrayList;

public class User {
    private String email;
    private String name;
    private String password;

    private List<CharacterSheet> sheets;


    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;

        sheets = new ArrayList<>()
    }


    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public List<CharacterSheet> getSheets() { return sheets; }

    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
}
