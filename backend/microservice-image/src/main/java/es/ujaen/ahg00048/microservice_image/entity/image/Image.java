package es.ujaen.ahg00048.microservice_image.entity.image;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@NoArgsConstructor
@Document("images")
public class Image {
    private String id;

    @Email @NotBlank
    private String userId;

    @NotBlank
    private String name;
    @Transient
    private byte[] data;
    @Positive
    private int width;
    @Positive
    private int height;

    private ImageType type;

    public Image(String userId, String name, byte[] data, int width, int height, ImageType type) {
        id = new ObjectId().toString();

        this.userId = userId;
        this.name = name;
        this.data = data;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    public Image(Image other) {
        id = other.id;
        userId = other.userId;
        name = other.name;
        data = other.data;
        width = other.width;
        height = other.height;
        type = other.type;
    }
}
