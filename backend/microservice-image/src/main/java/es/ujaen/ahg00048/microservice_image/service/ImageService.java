package es.ujaen.ahg00048.microservice_image.service;

import es.ujaen.ahg00048.microservice_image.exception.ImageFormatException;
import es.ujaen.ahg00048.microservice_image.exception.ImageOverSizedException;
import es.ujaen.ahg00048.microservice_image.rest.DTO.ImageWrap;
import es.ujaen.ahg00048.microservice_image.utils.ImageUtils;
import jakarta.validation.Valid;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import es.ujaen.ahg00048.microservice_image.entity.Image;
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

    private final static String LOCK_IMAGE_KEY_BASE = "image:";
    private final static int TIMEOUT_AMOUNT = 1;
    private final static TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;


    private final static String[] MIME_TYPES_ALLOWED = {"image/jpeg","image/png"};
    @Value("${app.image.max.sizeInKB}")
    private int MAX_IMAGE_SIZE_IN_KB;


    /**
     * @return the ids of the images belonging to the user
     */
    public List<String> getUserImagesIds(@Email @NotBlank String userId) {
        return _imagesMongoRep.findAllByUserId(userId).stream().map(Image::getId).toList();
    }

    /**
     * @return the access url of the image
     */
    @Transactional
    public String getImageUrl(@NotNull String id)
            throws ImageRegistrationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_IMAGE_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();
            locked = true;

            Image image = _imagesMongoRep.findById(id).orElseThrow(ImageRegistrationException::new);

            return _imagesMinIORep.getPreSignedUrl(image.getPath(), image.getType());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    /**
     * @return the pair id, url of every image of the user
     */
    @Transactional
    public List<ImageWrap> getImagesUrls(List<String> imagesIds)
            throws ImageRegistrationException,
            IllegalStateException {
        List<Lock> locks = new ArrayList<>(imagesIds.size());
        for (String id : imagesIds) {
            locks.add(_lockRegistry.obtain(LOCK_IMAGE_KEY_BASE + id));
        }

        int locked = 0;
        try {
            while (locked < locks.size()) {
                if (!locks.get(locked).tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                    throw new IllegalStateException();

                locked++;
            }

            List<Image> images = _imagesMongoRep.findAllById(imagesIds);
            List<ImageWrap> imageWraps = new ArrayList<>(images.size());

            for (Image img : images) {
                imageWraps.add(new ImageWrap(img.getId(), _imagesMinIORep.getPreSignedUrl(img.getPath(), img.getType())));
            }

            return imageWraps;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            for (int i = 0; i < locked; i++) {
                locks.get(i).unlock();
            }
        }
    }

    /**
     * @return the id of the image
     */
    @Transactional
    public String saveImage(@Email @NotBlank String userId, @Valid @NotNull MultipartFile file)
            throws InvalidOperationException, ImageOverSizedException, ImageFormatException,
            IllegalStateException {
        if (file.getSize() >= (MAX_IMAGE_SIZE_IN_KB * 1024L))
            throw new ImageOverSizedException();

        if (file.getContentType() == null ||
                file.getOriginalFilename() == null ||
                !Arrays.asList(MIME_TYPES_ALLOWED).contains(file.getContentType()) ||
                !ImageUtils.hasImageExtension(ImageUtils.parseFileName(file.getOriginalFilename())))
            throw new ImageFormatException();

        List<Image> userImages = _imagesMongoRep.findAllByUserId(userId);

        if (userImages.size() >= MAX_IMAGES_PER_USER)
            throw new InvalidOperationException();

        Image image = new Image(userId, ImageUtils.parseFileName(file.getOriginalFilename()), file.getContentType());

        _imagesMongoRep.insert(image);

        byte[] fileData = ImageUtils.optimizeImage(file);

        _imagesMinIORep.save(image.getPath(), fileData, image.getType());

        return image.getId();
    }

    @Transactional
    public void deleteImage(@Email @NotBlank String userId, @NotNull String id)
            throws ImageRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_IMAGE_KEY_BASE + id);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();
            locked = true;

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
            if (locked)
                lock.unlock();
        }
    }

    @Profile("test")
    @Transactional
    public void dropAllImages()
            throws ImageRegistrationException {
        List<Image> images = _imagesMongoRep.findAll();

        List<String> paths = images.stream().map(Image::getPath).toList();

        _imagesMongoRep.deleteAll();

        _imagesMinIORep.deleteAll(paths);
    }

    @Profile("test")
    @Transactional
    public List<Image> getUserImagesTest(@Email @NotBlank String userId)
            throws ImageRegistrationException {
        return _imagesMongoRep.findAllByUserId(userId);
    }
}
