package dev.videoanalysissystem.study.dto;

public record StudyVideoAnalysisAIRequest(
        String callbackURL,
        String videoURL,
        Long analysisStartFrame,
        Long analysisLastFrame
) {
}
