package dev.videoanalysissystem.study.dto;

import dev.videoanalysissystem.study.domain.StudyRecord;

public record StudyVideoAnalysisFinishEvent(
        StudyRecord studyRecord
) {
}
