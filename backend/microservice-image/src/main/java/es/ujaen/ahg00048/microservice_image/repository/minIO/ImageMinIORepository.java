package es.ujaen.ahg00048.microservice_image.repository.minIO;

import es.ujaen.ahg00048.microservice_image.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

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

    private static final int EXPIRE_TIME = 2;
    private static final TimeUnit EXPIRE_UNIT = TimeUnit.HOURS;


    public void save(String path, MultipartFile file) throws RuntimeException {
        try (InputStream stream = file.getInputStream()) {
            String contentType = file.getContentType();
            long size = file.getSize();

            _minioClient.putObject(PutObjectArgs.builder()
                    .bucket(_minioConfig.getBucketName())
                    .object(path)
                    .stream(stream, size, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            log.error("[MinIO] Error saving object.");
            throw new RuntimeException(e);
        }
    }

    public byte[] findByPath(String path) throws RuntimeException  {
        try (InputStream stream = _minioClient.getObject(GetObjectArgs.builder()
                .bucket(_minioConfig.getBucketName())
                .object(path)
                .build())) {
            return stream.readAllBytes();
        } catch (Exception e) { // Conversion to runtimeExc for transactional annotation
            log.error("[MinIO] Error getting object.");
            throw new RuntimeException(e);
        }
    }

    public void delete(String path) throws RuntimeException {
        try {
            _minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(_minioConfig.getBucketName())
                    .object(path)
                    .build());
        } catch (Exception e) {
            log.error("[MinIO] Error deleting object.");
            throw new RuntimeException(e);
        }
    }

    public List<String> deleteAll(List<String> paths) throws RuntimeException {
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
            throw new RuntimeException(e);
        }
    }

    public String getPreSignedUrl(String path, String contentType) throws RuntimeException {
        try {
            Map<String, String> reqParams = new HashMap<String, String>();
            reqParams.put("response-content-type", contentType);

            return _minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(_minioConfig.getBucketName())
                            .object(path)
                            .expiry(EXPIRE_TIME, EXPIRE_UNIT)
                            .extraQueryParams(reqParams)
                            .build());
        } catch (Exception e) {
            log.error("[MinIO]: Error at creating preSigned Url.");
            throw new RuntimeException(e);
        }
    }
}
