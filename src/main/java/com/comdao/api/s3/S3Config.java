package com.comdao.api.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class S3Config {
    private final MinioProperties properties;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(properties.getUrl()))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())
                ))
                .forcePathStyle(true)
                .build();
    }

    @Bean
    @Order(1)
    public ApplicationRunner minioInit(S3Client s3Client) {
        return args -> {
            Set<String> existing = s3Client.listBuckets().buckets()
                    .stream()
                    .map(Bucket::name)
                    .collect(Collectors.toSet());

            for (String bucket : properties.getBuckets()) {
                if (!existing.contains(bucket)) {
                    s3Client.createBucket(CreateBucketRequest.builder()
                            .bucket(bucket)
                            .build());

                    s3Client.putBucketPolicy(PutBucketPolicyRequest.builder()
                            .bucket(bucket)
                            .policy(publicPolicy(bucket))
                            .build());

                    System.out.println("Created bucket: " + bucket);
                }
            }
        };
    }

    private String publicPolicy(String bucket) {
        return """
                    {
                      "Version": "2012-10-17",
                      "Statement": [{
                        "Effect": "Allow",
                        "Principal": "*",
                        "Action": "s3:GetObject",
                        "Resource": "arn:aws:s3:::%s/*"
                      }]
                    }
                """.formatted(bucket);
    }
}
