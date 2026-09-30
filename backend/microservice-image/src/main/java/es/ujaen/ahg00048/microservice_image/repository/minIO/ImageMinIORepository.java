package es.ujaen.ahg00048.microservice_image.repository.minIO;

import es.ujaen.ahg00048.microservice_image.config.MinioConfig;
import es.ujaen.ahg00048.microservice_image.exception.ImageRegistrationException;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Repository
public class ImageMinIORepository {
    @Autowired
    private MinioClient _minioClient;

    @Autowired
    private MinioConfig _minioConfig;

    private static final int PRESIGNED_URL_EXPIRE_TIME = 2;
    private static final TimeUnit PRESIGNED_URL_EXPIRE_UNIT = TimeUnit.MINUTES;


    public void save(String path, byte[] data, String contentType) throws ImageRegistrationException {
        try (InputStream stream = new ByteArrayInputStream(data)) {

            _minioClient.putObject(PutObjectArgs.builder()
                    .bucket(_minioConfig.getBucketName())
                    .object(path)
                    .stream(stream, data.length, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            log.error("[MinIO] Error saving object.");
            throw new ImageRegistrationException();
        }
    }

    public byte[] findByPath(String path) throws ImageRegistrationException  {
        try (InputStream stream = _minioClient.getObject(GetObjectArgs.builder()
                .bucket(_minioConfig.getBucketName())
                .object(path)
                .build())) {
            return stream.readAllBytes();
        } catch (Exception e) { // Conversion to runtimeExc for transactional annotation
            log.error("[MinIO] Error getting object.");
            throw new ImageRegistrationException();
        }
    }

    public void delete(String path) throws ImageRegistrationException {
        try {
            _minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(_minioConfig.getBucketName())
                    .object(path)
                    .build());
        } catch (Exception e) {
            log.error("[MinIO] Error deleting object.");
            throw new ImageRegistrationException();
        }
    }

    public List<String> deleteAll(List<String> paths) throws ImageRegistrationException {
        List<DeleteObject> delObjects = paths.stream().map(DeleteObject::new).toList();

        Iterable<Result<DeleteError>> results = _minioClient.removeObjects(RemoveObjectsArgs.builder()
                .bucket(_minioConfig.getBucketName())
                .objects(delObjects)
                .build());
        try {
            List<String> persistentPaths = new ArrayList<>();
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();

                persistentPaths.add(error.objectName());

                log.error("[MinIO]: Error at deleting object with id %s in bucket with name %s.".formatted(error.objectName(), error.bucketName()));
            }

            return persistentPaths;
        } catch (Exception e) {
            log.error("[MinIO]: Error, unexpected exception at results handling in objects deletion.");
            throw new ImageRegistrationException();
        }
    }

    public String getPreSignedUrl(String path, String contentType) throws ImageRegistrationException {
        try {
            Map<String, String> reqParams = new HashMap<String, String>();
            reqParams.put("response-content-type", contentType);

            return _minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(_minioConfig.getBucketName())
                            .object(path)
                            .expiry(PRESIGNED_URL_EXPIRE_TIME, PRESIGNED_URL_EXPIRE_UNIT)
                            .extraQueryParams(reqParams)
                            .build());
        } catch (Exception e) {
            log.error("[MinIO]: Error at creating preSigned Url.");
            throw new ImageRegistrationException();
        }
    }
}
