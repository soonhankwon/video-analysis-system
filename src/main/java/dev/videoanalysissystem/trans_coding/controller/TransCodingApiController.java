package dev.videoanalysissystem.trans_coding.controller;

import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import dev.videoanalysissystem.trans_coding.dto.FinishUploadRequest;
import dev.videoanalysissystem.trans_coding.dto.PreSignedUploadInitiateRequest;
import dev.videoanalysissystem.trans_coding.dto.PreSignedUrlAbortRequest;
import dev.videoanalysissystem.trans_coding.dto.PreSignedUrlCreateRequest;
import dev.videoanalysissystem.trans_coding.service.TransCodingService;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trans-coding")
public class TransCodingApiController {

    private final TransCodingService transCodingService;
    private final Map<String, String> map = new HashMap<>();

    //Multipart 업로드 시작
    @PostMapping("/initiate-upload")
    public ResponseEntity<InitiateMultipartUploadResult> initUpload(
            @RequestBody PreSignedUploadInitiateRequest request) {
        String objectName = UUID.randomUUID().toString();
        map.put("userA", objectName);

        InitiateMultipartUploadResult initiateMultipartUploadResult = transCodingService.initUpload(objectName,
                request);
        return ResponseEntity.ok().body(initiateMultipartUploadResult);
    }

    //PresignedURL 발급 - PartNumber의 ETag를 헤더에서 확인할 수 있다.
    @PostMapping("/presigned-url")
    public ResponseEntity<URL> presignedUrl(@RequestBody PreSignedUrlCreateRequest request) {
        String objectName = map.get("userA");
        URL url = transCodingService.presignedUrl(objectName, request);
        return ResponseEntity.ok().body(url);
    }

    //Multipart 업로드 완료 - 클라이언트는 PartNumber와 ETag를 매칭해서 업로드 해야한다.
    @PostMapping("/complete-upload")
    public ResponseEntity<CompleteMultipartUploadResult> completeMultipartUpload(
            @RequestBody FinishUploadRequest request) {
        String objectName = map.get("userA");
        CompleteMultipartUploadResult completeMultipartUploadResult = transCodingService.completeUpload(objectName,
                request);
        return ResponseEntity.ok().body(completeMultipartUploadResult);
    }

    //Multipart 업로드 취소
    @PostMapping("/abort-upload")
    public ResponseEntity<String> abortMultipartUpload(@RequestBody PreSignedUrlAbortRequest request) {
        String objectName = map.get("userA");
        transCodingService.abortMultipartUpload(objectName, request);
        return ResponseEntity.ok().body("canceled");
    }
}
