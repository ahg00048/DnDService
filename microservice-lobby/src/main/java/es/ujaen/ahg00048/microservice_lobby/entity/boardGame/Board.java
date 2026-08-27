package es.ujaen.ahg00048.microservice_lobby.entity.boardGame;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Board {
    private String background;
    private final List<Piece> pieces;
    private int scale;


    public Board(int scale, String background) {
        pieces = new ArrayList<>();

        this.background = background;
        this.scale = scale;
    }

    public Board(int scale) {
        pieces = new ArrayList<>();
        background = "";

        this.scale = scale;
    }


    public void clear() { pieces.clear(); }
}
