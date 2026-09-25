package es.ujaen.ahg00048.microservice_image.entity.image;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;


@Getter
@Setter
public class Image {
    private String id;

    private String userId;

    private String name;
    private String path;
    private byte[] data;
    private int width, height;
    private ImageType type;

    public Image(String userId, String name, String path, byte[] data, int width, int height, ImageType type) {
        id = new ObjectId().toString();

        this.userId = userId;
        this.name = name;
        this.path = path;
        this.data = data;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    public Image(Image other) {
        id = other.id;
        userId = other.userId;
        name = other.name;
        path = other.path;
        data = other.data;
        width = other.width;
        height = other.height;
        type = other.type;
    }
}
