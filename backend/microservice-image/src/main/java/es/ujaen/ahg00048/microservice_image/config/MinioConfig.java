package es.ujaen.ahg00048.microservice_image.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MinioConfig {

    @Value("${minio.hostname}")
    private String hostname;

    @Value("${minio.port}")
    private int port;

    @Value("${minio.secure}")
    private boolean secure;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Getter
    @Value("${minio.bucket.name}")
    private String bucketName;


    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(hostname, port, secure)
                .credentials(accessKey, secretKey)
                .build();

        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()))
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

        return minioClient;
    }
}
