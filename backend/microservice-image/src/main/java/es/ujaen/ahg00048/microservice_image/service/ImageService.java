package es.ujaen.ahg00048.microservice_image.service;

import es.ujaen.ahg00048.microservice_image.entity.image.ImageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ujaen.ahg00048.microservice_image.entity.image.Image;
import es.ujaen.ahg00048.microservice_image.exception.ImageRegistrationException;
import es.ujaen.ahg00048.microservice_image.exception.InvalidOperationException;


@Service
@Validated
public class ImageService {
    private Map<String, Image> _imagesRep = new HashMap<>();

    private static int MAX_IMAGES_PER_USER;

    @Autowired
    public ImageService(@Value("${app.user.max.images}") int maxImagesPerUser) {
        MAX_IMAGES_PER_USER = maxImagesPerUser;
    }


    public int MAX_IMAGES_PER_USER() {
        return MAX_IMAGES_PER_USER;
    }

    public Image getImage(@NotNull String id) throws ImageRegistrationException {
        if (!_imagesRep.containsKey(id))
            throw new ImageRegistrationException();

        return _imagesRep.get(id);
    }

    public List<Image> getUserImages(@Email @NotBlank String userId) {
        return _imagesRep.values().stream().filter(img -> img.getUserId().equals(userId)).toList();
    }

    public Image saveImage(@Email @NotBlank String userId, @Valid Image image) throws InvalidOperationException {
        List<Image> userImages = _imagesRep.values().stream().filter(img -> img.getUserId().equals(userId)).toList();

        if (userImages.size() >= MAX_IMAGES_PER_USER)
            throw new InvalidOperationException();

        image.setUserId(userId);

        return _imagesRep.put(image.getId(), new Image(image));
    }

    public void deleteImage(@Email @NotBlank String userId, @NotNull String id) throws ImageRegistrationException, InvalidOperationException {
        if (!_imagesRep.containsKey(id))
            throw new ImageRegistrationException();

        Image image = _imagesRep.get(id);

        if (!image.getUserId().equals(userId))
            throw new InvalidOperationException();

        _imagesRep.remove(image.getId());
    }
}
