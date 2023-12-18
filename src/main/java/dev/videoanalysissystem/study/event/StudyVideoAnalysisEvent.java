package dev.videoanalysissystem.study.event;

import dev.videoanalysissystem.study.domain.StudyRecord;

public record StudyVideoAnalysisEvent(
        StudyRecord studyRecord
) {
}
