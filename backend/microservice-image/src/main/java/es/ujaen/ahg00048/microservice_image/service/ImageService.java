package es.ujaen.ahg00048.microservice_image.service;

import es.ujaen.ahg00048.microservice_image.entity.image.ImageType;
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


    public Image getImage(String id) throws ImageRegistrationException {
        if (!_imagesRep.containsKey(id))
            throw new ImageRegistrationException();

        return _imagesRep.get(id);
    }

    public List<Image> getUserImages(String userId) {
        return _imagesRep.values().stream().filter(img -> img.getUserId().equals(userId)).toList();
    }

    public Image saveImage(String userId, byte[] imageData, ImageType type, String name, int width, int height) throws InvalidOperationException {
        List<Image> userImages = _imagesRep.values().stream().filter(img -> img.getUserId().equals(userId)).toList();

        if (userImages.size() >= MAX_IMAGES_PER_USER)
            throw new InvalidOperationException();

        Image image = new Image(userId, name, "path", imageData, width, height, type);

        return _imagesRep.put(image.getId(), image);
    }

    public void deleteImage(String userId, String id) throws ImageRegistrationException, InvalidOperationException {
        if (!_imagesRep.containsKey(id))
            throw new ImageRegistrationException();

        Image image = _imagesRep.get(id);

        if (!image.getUserId().equals(userId))
            throw new InvalidOperationException();

        _imagesRep.remove(image.getId());
    }
}
