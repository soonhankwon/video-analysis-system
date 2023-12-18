package dev.videoanalysissystem.study.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudyVideoAnalysisProgressStatus {

    private Integer studyMin;

    private StudyVideoAnalysisStatus studyVideoAnalysisStatus;

    private Integer errorCount;

    public void increaseStudyMin(int min) {
        this.studyMin += min;
    }

    public void increaseErrorCount() {
        this.errorCount++;
    }

    public void decreaseErrorCount() {
        this.errorCount--;
    }

    public boolean hasProgressError() {
        return this.errorCount > 0;
    }

    public void updateStatusProgressing() {
        this.studyVideoAnalysisStatus = StudyVideoAnalysisStatus.PROGRESSING;
    }

    public void updateStatusCompleted() {
        this.studyVideoAnalysisStatus = StudyVideoAnalysisStatus.COMPLETED;
    }

    public String noticeAnalysisProgress() {
        if (this.studyVideoAnalysisStatus == StudyVideoAnalysisStatus.COMPLETED && this.hasProgressError()) {
            return "분석이 종료됬지만, 예외처리 중 입니다.";
        }
        if (this.studyVideoAnalysisStatus == StudyVideoAnalysisStatus.PROGRESSING) {
            return "분석중";
        }
        if (this.studyVideoAnalysisStatus == StudyVideoAnalysisStatus.READY) {
            return "분석대기중";
        }
        if (this.studyVideoAnalysisStatus == StudyVideoAnalysisStatus.COMPLETED && !this.hasProgressError()) {
            return "분석완료";
        }
        // 로깅필요
        throw new RuntimeException();
    }
}
