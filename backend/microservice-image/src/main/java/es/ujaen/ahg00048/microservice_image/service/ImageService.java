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
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.*;
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

    @Autowired
    private RabbitTemplate _rabbitTemplate;


    @Getter
    @Value("${app.user.max.images}")
    private int MAX_IMAGES_PER_USER;


    @Autowired
    private RedisLockRegistry _lockRegistry;

    private final static String LOCK_USER_KEY_BASE = "images:";
    private final static int TIMEOUT_AMOUNT = 1;
    private final static TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;


    private final static String[] MIME_TYPES_ALLOWED = {"image/jpeg","image/png"};
    @Value("${app.image.max.sizeInKB}")
    private int MAX_IMAGE_SIZE_IN_KB;


    /**
     * @return the ids of the images belonging to the user
     */
    @Transactional
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
        Image image = _imagesMongoRep.findById(id).orElseThrow(ImageRegistrationException::new);
        Lock lock = _lockRegistry.obtain(LOCK_USER_KEY_BASE + image.getUserId());
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();
            locked = true;

            image = _imagesMongoRep.findById(id).orElseThrow(ImageRegistrationException::new);

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
        List<Image> images = _imagesMongoRep.findAllById(imagesIds);
        Set<String> userIds = new HashSet<>();

        for (Image img : images) {
            String userId = img.getUserId();
            if (!userIds.contains(userId)) {
                userIds.add(userId);
                locks.add(_lockRegistry.obtain(LOCK_USER_KEY_BASE + userId));
            }
        }

        int locked = 0;
        try {
            while (locked < locks.size()) {
                if (!locks.get(locked).tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                    throw new IllegalStateException();

                locked++;
            }

            images = _imagesMongoRep.findAllById(imagesIds);
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
        Lock lock = _lockRegistry.obtain(LOCK_USER_KEY_BASE + userId);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();
            locked = true;
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    @Transactional
    public void deleteImage(@Email @NotBlank String userId, @NotNull String id)
            throws ImageRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_USER_KEY_BASE + userId);
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

            _rabbitTemplate.send(new Message(id.getBytes()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    /// used by RabbitMQ only -----------------------------------------------------------------------------------------------------------

    @Transactional
    public void removeUser_admin(String userId)
            throws IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_USER_KEY_BASE + userId);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();
            locked = true;

            List<Image> images = _imagesMongoRep.findAllByUserId(userId);

            _imagesMinIORep.deleteAll(images.stream().map(Image::getPath).toList());

            List<String> imagesIds = images.stream().map(Image::getId).toList();

            _imagesMongoRep.deleteAll(images);

            for (String id : imagesIds) {
                _rabbitTemplate.send(new Message(id.getBytes()));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    /// Test only -----------------------------------------------------------------------------------------------------------

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
