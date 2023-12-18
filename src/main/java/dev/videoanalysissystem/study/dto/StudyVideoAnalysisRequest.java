package dev.videoanalysissystem.study.dto;

public record StudyVideoAnalysisRequest(
        String callbackURL,
        String videoURL,
        Long analysisStartFrame,
        Long analysisLastFrame,
        boolean isLastPart
) {
    public StudyVideoAnalysisAIRequest ofAIRequest() {
        return new StudyVideoAnalysisAIRequest(this.callbackURL, this.videoURL, this.analysisStartFrame,
                this.analysisLastFrame);
    }
}
