package dev.videoanalysissystem.study.service;

import dev.videoanalysissystem.study.domain.StudyRecord;
import dev.videoanalysissystem.study.domain.StudyVideoAnalysisProgressStatus;
import dev.videoanalysissystem.study.event.StudyVideoAnalysisEvent;
import dev.videoanalysissystem.study.repository.StudyRecordRepository;
import dev.videoanalysissystem.study.repository.StudyVideoAnalysisProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRecordRepository studyRecordRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final StudyVideoAnalysisProgressRepository studyVideoAnalysisProgressRepository;

    public String analysisStudyVideo(Long videoId) {
        // 동영상을 AI 분석 서비스로 요청
        StudyRecord studyRecord = studyRecordRepository.findById(videoId)
                .orElseThrow(IllegalArgumentException::new);

        // 동영상 AI 분석 이벤트 발행
        eventPublisher.publishEvent(new StudyVideoAnalysisEvent(studyRecord));
        // 프론트에서 videoUrl을 리턴받아 분석상태 확인 API를 사용할 수 있도록 함
        return studyRecord.getVideoUrl();
    }

    public String getStudyAnalysisStatus(String videoUrl) {
        StudyVideoAnalysisProgressStatus studyAnalysisStatus = studyVideoAnalysisProgressRepository.getOrDefault(
                videoUrl);
        return studyAnalysisStatus.noticeAnalysisProgress();
    }
}
