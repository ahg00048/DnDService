package es.ujaen.ahg00048.DnDService.entities;


public class Image {
    private String id;  // sera usado para persistencia en el sistema de archivos local
    private String name;
    private Byte[] data;
    private int width, height;


    public Image(String name, Byte[] data, int width, int height) {
        this.name = name;
        this.data = data;
        this.width = width;
        this.height = height;
    }


    public String getId() { return id; }
    public String getName() { return name; }
    public Byte[] getData() { return data; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void getData(Byte[] data) { this.data = data; }
    public void getWidth(int width) { this.width = width; }
    public void getHeight(int height) { this.height = height; }
}
