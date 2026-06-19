package com.comdao.api.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService {
    private final S3Client s3Client;
    private final MinioProperties properties;

    @Value("${app.s3-base-url}")
    private String s3BaseUrl;

    @Bean
    @Order(100)
    public CommandLineRunner log() {
        return args -> {
            System.out.println("----------------------------------------Image Upload Service------------------------------------");
            System.out.println(s3BaseUrl);
            System.out.println(properties);
        };
    }

    public String uploadImage(MultipartFile file, String bucket) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(filename)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        return s3BaseUrl + "/" + bucket + "/" + filename;
    }

    public void delete(String url) {
        if (url == null) return;
        // extract bucket and filename from url
        // url format: http://minio:9000/bucket/filename
        String[] parts = url.replace(properties.getUrl() + "/", "").split("/", 2);
        String bucket = parts[0];
        String filename = parts[1];

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build());
    }
}
