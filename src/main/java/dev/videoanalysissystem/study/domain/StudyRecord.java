package dev.videoanalysissystem.study.domain;

import dev.videoanalysissystem.study.dto.StudyVideoUpdateRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class StudyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //영상이 저장된 URL(S3), 촬영이 끝난후 non-null
    private String videoUrl;

    //영상의 길이(프레임수), 촬영이 끝난후 non-null
    private Integer videoFrameCount;

    //공부의 시간, 최종적으로 입력되어야하는 값
    private Integer studyMin;

    public void completeAnalysis(StudyVideoUpdateRequest request) {
        if (this.studyMin == null) {
            this.studyMin = request.studyMinute();
            return;
        }
        this.studyMin += request.studyMinute();

    }

    public void updateStudyMin(Integer min) {
        this.studyMin = min;
    }
}
