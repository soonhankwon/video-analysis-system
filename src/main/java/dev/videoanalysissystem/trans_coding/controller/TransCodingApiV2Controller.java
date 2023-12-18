package dev.videoanalysissystem.trans_coding.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/trans-coding")
public class TransCodingApiV2Controller {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3Client;
    private final Map<String, String> map = new HashMap<>();

    @PostMapping("/multipart-files")
    public ResponseEntity<String> uploadMultipleFile(@RequestPart MultipartFile file) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        log.info("fileContentType={}", file.getContentType());
        log.info("fileSize={}", file.getSize());

        String objectName = UUID.randomUUID().toString();
        map.put("userA", objectName);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectName, file.getInputStream(),
                objectMetadata);
        amazonS3Client.putObject(putObjectRequest);

        return ResponseEntity.ok().body("uploaded");
    }

    @PostMapping("/multipart-files/list")
    public ResponseEntity<String> uploadMultipleFileV2(@RequestPart List<MultipartFile> files) throws IOException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            files.forEach(f -> {
                executor.submit(() -> {
                    ObjectMetadata objectMetadata = new ObjectMetadata();
                    objectMetadata.setContentType(f.getContentType());
                    objectMetadata.setContentLength(f.getSize());
                    String objectName = UUID.randomUUID().toString();
                    try {
                        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectName, f.getInputStream(),
                                objectMetadata);
                        amazonS3Client.putObject(putObjectRequest);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        }

        return ResponseEntity.ok().body("uploaded");
    }
}
