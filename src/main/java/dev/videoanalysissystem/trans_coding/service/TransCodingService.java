package dev.videoanalysissystem.trans_coding.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import dev.videoanalysissystem.trans_coding.dto.FinishUploadRequest;
import dev.videoanalysissystem.trans_coding.dto.PreSignedUploadInitiateRequest;
import dev.videoanalysissystem.trans_coding.dto.PreSignedUrlAbortRequest;
import dev.videoanalysissystem.trans_coding.dto.PreSignedUrlCreateRequest;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransCodingService {

    private final AmazonS3 amazonS3Client;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public InitiateMultipartUploadResult initUpload(String objectName, PreSignedUploadInitiateRequest request) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(request.fileSize());
        objectMetadata.setContentType(URLConnection.guessContentTypeFromName(request.fileType()));

        return amazonS3Client.initiateMultipartUpload(
                new InitiateMultipartUploadRequest(bucket, objectName, objectMetadata));
    }

    public URL presignedUrl(String objectName, PreSignedUrlCreateRequest request) {
        Date expirationTime = Date.from(LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant());
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket,
                objectName).withMethod(HttpMethod.PUT).withExpiration(expirationTime);

        generatePresignedUrlRequest.addRequestParameter("uploadId", request.uploadId());
        generatePresignedUrlRequest.addRequestParameter("partNumber", request.partNumber().toString());
        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }

    public CompleteMultipartUploadResult completeUpload(String objectName, FinishUploadRequest request) {
        List<PartETag> partETags = request.parts().stream().map(p -> new PartETag(p.partNumber(), p.eTag()))
                .collect(Collectors.toList());

        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucket,
                objectName, request.uploadId(), partETags);

        return amazonS3Client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    public void abortMultipartUpload(String objectName, PreSignedUrlAbortRequest request) {
        AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(bucket, objectName,
                request.uploadId());

        amazonS3Client.abortMultipartUpload(abortMultipartUploadRequest);
    }
}
