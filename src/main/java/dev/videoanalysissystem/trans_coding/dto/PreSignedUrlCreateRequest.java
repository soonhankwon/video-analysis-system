package dev.videoanalysissystem.trans_coding.dto;

public record PreSignedUrlCreateRequest(
        String uploadId,
        Integer partNumber
) {
}
