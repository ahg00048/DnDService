package es.ujaen.ahg00048.microservice_image.rest;


import es.ujaen.ahg00048.microservice_image.rest.DTO.ImageWrap;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import es.ujaen.ahg00048.microservice_image.exception.ImageFormatException;
import es.ujaen.ahg00048.microservice_image.exception.ImageOverSizedException;
import es.ujaen.ahg00048.microservice_image.exception.ImageRegistrationException;
import es.ujaen.ahg00048.microservice_image.exception.InvalidOperationException;
import es.ujaen.ahg00048.microservice_image.service.ImageService;


@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    @Autowired
    private ImageService _imageService;


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public void constraintValidationViolationException() {}


    @GetMapping("/MaxAllowedPerUser")
    public ResponseEntity<Integer> getMaxImagesPerUserAllowed() {
        return ResponseEntity.ok(_imageService.getMAX_IMAGES_PER_USER());
    }

    @GetMapping
    public ResponseEntity<List<?>> getImageUrls(@RequestParam(value = "userId", required = false) String userId,
                                                @RequestParam(value = "imagesIds", required = false) List<String> imagesIds) {
        try {
            if (userId != null) {
                return ResponseEntity.ok(_imageService.getUserImagesIds(userId));
            } else if (imagesIds != null) {
                return ResponseEntity.ok(_imageService.getImagesUrls(imagesIds));
            } else{
                return ResponseEntity.badRequest().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addImage(@RequestParam(value = "userId", required = true) String userId,
                                           @RequestParam(value = "image", required = true) MultipartFile file) {
        try {
            return ResponseEntity.ok(_imageService.saveImage(userId, file));
        } catch (ImageFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } catch (ImageOverSizedException e) {
            return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getImage(@PathVariable(value = "id") String id) {
        try {
            return ResponseEntity.ok(_imageService.getImageUrl(id));
        } catch (ImageRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeImage(@RequestParam(value = "userId", required = true) String userId,
                                            @PathVariable(value = "id") String id) {
        try {
            _imageService.deleteImage(userId, id);
            return ResponseEntity.ok().build();
        } catch (ImageRegistrationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
