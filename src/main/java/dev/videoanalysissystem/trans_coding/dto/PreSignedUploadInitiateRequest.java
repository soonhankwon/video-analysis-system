package dev.videoanalysissystem.trans_coding.dto;

public record PreSignedUploadInitiateRequest(
        String originalFileName,
        String fileType,
        Long fileSize
) {
}
