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
    private String path;
    @NotBlank
    private String name;
    @NotBlank
    private String type;

    @Transient
    private byte[] data;

    public Image(String userId, String name, String type) {
        id = new ObjectId().toString();

        this.userId = userId;
        this.name = name;
        this.type = type;

        this.path = "/" + userId + "/" + id;
    }

    public Image(Image other) {
        id = other.id;
        path = other.path;
        userId = other.userId;
        name = other.name;
        type = other.type;
        data = other.data;
    }
}
