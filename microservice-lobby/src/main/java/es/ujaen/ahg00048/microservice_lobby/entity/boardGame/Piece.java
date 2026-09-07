package es.ujaen.ahg00048.microservice_lobby.entity.boardGame;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Piece {
    private int id;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    private float x = 0.5f;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    private float y = 0.5f;

    @NotNull
    private String imageId = "";

    @PositiveOrZero
    private int hp = 10;
    @Positive
    private int maxHp = 10;


    public Piece() {
    }

    public Piece(int id) {
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
