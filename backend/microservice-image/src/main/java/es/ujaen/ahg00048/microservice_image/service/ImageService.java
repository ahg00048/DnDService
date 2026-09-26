package es.ujaen.ahg00048.microservice_image.service;

import es.ujaen.ahg00048.microservice_image.repository.fileSystem.ImageFileSystemRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.tomcat.autoconfigure.TomcatServerProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import es.ujaen.ahg00048.microservice_image.entity.image.Image;
import es.ujaen.ahg00048.microservice_image.exception.ImageRegistrationException;
import es.ujaen.ahg00048.microservice_image.exception.InvalidOperationException;
import es.ujaen.ahg00048.microservice_image.repository.mongo.ImageMongoRepository;


@Service
@Validated
public class ImageService {
    @Autowired
    private ImageMongoRepository _imagesMongoRep;

    @Autowired
    private ImageFileSystemRepository _imagesFileSysRep;

    @Autowired
    private RedisLockRegistry _lockRegistry;

    private static int MAX_IMAGES_PER_USER;

    private final static String LOCK_KEY_BASE = "image:";
    private final static int TIMEOUT_AMOUNT = 1;
    private final static TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;


    @Autowired
    public ImageService(@Value("${app.user.max.images}") int maxImagesPerUser) {
        MAX_IMAGES_PER_USER = maxImagesPerUser;
    }


    public int MAX_IMAGES_PER_USER() {
        return MAX_IMAGES_PER_USER;
    }

    @Transactional
    public Image getImage(@NotNull String id)
            throws ImageRegistrationException, IOException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();

            Image image = _imagesMongoRep.findById(id).orElseThrow(ImageRegistrationException::new);

            image.setData(_imagesFileSysRep.findByPath(image.getId()));

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
        throws IOException {

        List<Image> images = _imagesMongoRep.findAllByUserId(userId);

        for (Image img : images) {
            img.setData(_imagesFileSysRep.findByPath(img.getId()));
        }

        return images;
    }

    @Transactional
    public Image saveImage(@Email @NotBlank String userId, @Valid Image image)
            throws InvalidOperationException, IOException {
        List<Image> userImages = _imagesMongoRep.findAllByUserId(userId);

        if (userImages.size() >= MAX_IMAGES_PER_USER)
            throw new InvalidOperationException();

        image.setUserId(userId);

        _imagesMongoRep.insert(image);

        _imagesFileSysRep.save(image.getId(), image.getData());

        return image;
    }

    @Transactional
    public void deleteImage(@Email @NotBlank String userId, @NotNull String id)
            throws ImageRegistrationException, InvalidOperationException, IOException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_KEY_BASE + id);
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();

            Image image = _imagesMongoRep.findById(id).orElseThrow(ImageRegistrationException::new);

            if (!image.getUserId().equals(userId))
                throw new InvalidOperationException();

            _imagesFileSysRep.delete(image.getId());

            _imagesMongoRep.deleteById(image.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Profile("test")
    @Transactional
    public void dropAllImages() throws IOException {
        List<Image> images = _imagesMongoRep.findAll();

        for (Image img : images) {
            _imagesFileSysRep.delete(img.getId());
        }

        _imagesMongoRep.deleteAll();
    }
}
