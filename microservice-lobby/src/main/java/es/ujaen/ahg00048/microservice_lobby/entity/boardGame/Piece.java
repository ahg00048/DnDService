package es.ujaen.ahg00048.microservice_lobby.entity.boardGame;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Piece {
    private int id;

    private float x, y;
    private String imageId;
    private int hp;
    private int maxHp;


    public Piece() {
        x = y = 0.0f;
        imageId = "";
        hp = maxHp = 10;
    }

    public Piece(int id) {
        x = y = 0.0f;
        imageId = "";
        hp = maxHp = 10;

        this.id = id;
    }

    public Piece(int id, float x, float y, int hp, int maxHp) {
        imageId = "";

        this.id = id;
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.maxHp = maxHp;
    }

    public Piece(int id, float x, float y, int hp, int maxHp, String imageId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.imageId = imageId;
        this.hp = hp;
        this.maxHp = maxHp;
    }


    @Override
    public boolean equals(Object obj) {
        return id == ((Piece) obj).id;
    }
}
