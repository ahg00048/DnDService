package es.ujaen.ahg00048.microservice_image.service;

import es.ujaen.ahg00048.microservice_image.exception.ImageFormatException;
import es.ujaen.ahg00048.microservice_image.exception.ImageOverSizedException;
import es.ujaen.ahg00048.microservice_image.utils.FileUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import es.ujaen.ahg00048.microservice_image.entity.image.Image;
import es.ujaen.ahg00048.microservice_image.exception.ImageRegistrationException;
import es.ujaen.ahg00048.microservice_image.exception.InvalidOperationException;
import es.ujaen.ahg00048.microservice_image.repository.mongo.ImageMongoRepository;
import es.ujaen.ahg00048.microservice_image.repository.minIO.ImageMinIORepository;


@Service
@Validated
public class ImageService {
    @Autowired
    private ImageMongoRepository _imagesMongoRep;

    @Autowired
    private ImageMinIORepository _imagesMinIORep;


    @Getter
    @Value("${app.user.max.images}")
    private int MAX_IMAGES_PER_USER;


    @Autowired
    private RedisLockRegistry _lockRegistry;

    private final static String LOCK_KEY_BASE = "image:";
    private final static int TIMEOUT_AMOUNT = 1;
    private final static TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;


    private final static String[] MIME_TYPES_ALLOWED = {"image/jpeg","image/png"};
    @Value("${app.image.max.sizeInKB}")
    private int MAX_IMAGE_SIZE_IN_KB;


    @Transactional
    public Image getImage(@NotNull String id)
            throws ImageRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();

            Image image = _imagesMongoRep.findById(id).orElseThrow(ImageRegistrationException::new);

            byte[] imageData = _imagesMinIORep.findByPath(image.getPath());

            image.setData(imageData);

            return image;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

    }

    @Transactional
    public List<Image> getUserImages(@Email @NotBlank String userId)
        throws ImageRegistrationException {

        List<Image> images = _imagesMongoRep.findAllByUserId(userId);

        for (Image img : images) {
            img.setData(_imagesMinIORep.findByPath(img.getPath()));
        }

        return images;
    }

    @Transactional
    public Image saveImage(@Email @NotBlank String userId, MultipartFile file)
            throws ImageRegistrationException, InvalidOperationException,
            ImageOverSizedException, ImageFormatException {
        long size = file.getSize();
        if (file.getSize() >= (MAX_IMAGE_SIZE_IN_KB * 1024L))
            throw new ImageOverSizedException();

        if (file.getContentType() == null ||
                file.getOriginalFilename() == null ||
                !Arrays.asList(MIME_TYPES_ALLOWED).contains(file.getContentType()) ||
                !FileUtils.hasImageExtension(FileUtils.parseFileName(file.getOriginalFilename())))
            throw new ImageFormatException();

        List<Image> userImages = _imagesMongoRep.findAllByUserId(userId);

        if (userImages.size() >= MAX_IMAGES_PER_USER)
            throw new InvalidOperationException();

        Image image = new Image(userId, FileUtils.parseFileName(file.getOriginalFilename()), file.getContentType());

        _imagesMongoRep.insert(image);

        _imagesMinIORep.save(image.getPath(), file);

        return image;
    }

    @Transactional
    public void deleteImage(@Email @NotBlank String userId, @NotNull String id)
            throws ImageRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();

            Image image = _imagesMongoRep.findById(id).orElseThrow(ImageRegistrationException::new);

            if (!image.getUserId().equals(userId))
                throw new InvalidOperationException();

            String imageId = image.getId();
            String imagePath = image.getPath();

            _imagesMongoRep.deleteById(imageId);

            _imagesMinIORep.delete(imagePath);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Profile("test")
    @Transactional
    public void dropAllImages() throws ImageRegistrationException {
        List<Image> images = _imagesMongoRep.findAll();

        List<String> paths = images.stream().map(Image::getPath).toList();

        _imagesMongoRep.deleteAll();

        _imagesMinIORep.deleteAll(paths);
    }
}
