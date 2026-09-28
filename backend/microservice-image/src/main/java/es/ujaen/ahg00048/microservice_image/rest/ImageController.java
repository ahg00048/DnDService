package es.ujaen.ahg00048.microservice_image.rest;

import es.ujaen.ahg00048.microservice_image.entity.image.Image;
import es.ujaen.ahg00048.microservice_image.exception.ImageRegistrationException;
import es.ujaen.ahg00048.microservice_image.exception.InvalidOperationException;
import es.ujaen.ahg00048.microservice_image.rest.DTO.ImageDTO;
import es.ujaen.ahg00048.microservice_image.rest.mapper.ImageMapper;
import es.ujaen.ahg00048.microservice_image.service.ImageService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController("/api/v1/images")
public class ImageController {

    @Autowired
    private ImageMapper _mapper;

    @Autowired
    private ImageService _imageService;


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public void validationConstraintViolationException() {}

    @GetMapping
    public ResponseEntity<List<ImageDTO>> getUserImages(@RequestParam(value = "userId", required = true) String userId) {
        try {
            _imageService.getUserImages(userId);
            return ResponseEntity.ok().build();
        } catch (ImageRegistrationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ImageDTO> addImage(@RequestParam(value = "userId", required = true) String userId,
                                         @RequestBody MultipartFile image) {
        try {
            _imageService.saveImage(userId, null);
            return ResponseEntity.ok().build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (ImageRegistrationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageDTO> getImage(@PathVariable(value = "id") String id) {
        try {
            _imageService.getImage(id);
            return ResponseEntity.ok().build();
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
