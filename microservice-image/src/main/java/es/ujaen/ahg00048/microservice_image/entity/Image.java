package es.ujaen.ahg00048.microservice_image.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Image {
    private String id;
    private String name;
    private Byte[] data;
    private int width, height;


    public Image(String name, Byte[] data, int width, int height) {
        this.name = name;
        this.data = data;
        this.width = width;
        this.height = height;
    }
}
