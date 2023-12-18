package dev.videoanalysissystem.trans_coding.dto;

import java.util.List;

public record FinishUploadRequest(
        String uploadId,
        List<Part> parts
) {
    public record Part(
            Integer partNumber,
            String eTag
    ) {

    }
}
