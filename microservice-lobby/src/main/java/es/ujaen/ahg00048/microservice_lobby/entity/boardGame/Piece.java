package es.ujaen.ahg00048.microservice_lobby.entity.boardGame;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Piece {
    private float x, y;
    private String imageId;
    private int hp;
    private int maxHp;


    public Piece(float x, float y, int hp, int maxHp) {
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.maxHp = maxHp;
    }

    public Piece(float x, float y, int hp, int maxHp, String imageId) {
        this.x = x;
        this.y = y;
        this.imageId = imageId;
        this.hp = hp;
        this.maxHp = maxHp;
    }
}
