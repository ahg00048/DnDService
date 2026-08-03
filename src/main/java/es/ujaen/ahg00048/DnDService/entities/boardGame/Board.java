package es.ujaen.ahg00048.DnDService.entities.boardGame;

import java.util.ArrayList;
import java.util.List;

import es.ujaen.ahg00048.DnDService.entities.Image;

public class Board {
    private Image background;
    private final List<Piece> pieces;
    private int scale;


    public Board(Image background, int scale) {
        pieces = new ArrayList<>();

        this.background = background;
        this.scale = scale;
    }


    public List<Piece> getPieces() { return pieces; }
    public int getScale() { return scale; }
    public Image getBackground() { return background; }

    public void setScale(int scale) { this.scale = scale; }
    public void setBackground(Image background) { this.background = background;}


    public void clear() { pieces.clear(); }
}
