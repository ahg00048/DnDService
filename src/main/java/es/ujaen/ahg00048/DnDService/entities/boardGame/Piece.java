package es.ujaen.ahg00048.DnDService.entities.boardGame;

import es.ujaen.ahg00048.DnDService.entities.Image;

public class Piece {
    private float x, y;
    private Image image;
    private int hp;
    private int maxHp;


    public Piece(float x, float y, Image image, int hp, int maxHp) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.hp = hp;
        this.maxHp = maxHp;
    }


    public float getX() { return x; }
    public float getY() { return y; }
    public Image getImage() { return image; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }

    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setImage(Image image) { this.image = image; }
    public void setHp(int hp) { this.hp = hp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
}
