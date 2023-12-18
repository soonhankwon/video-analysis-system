package dev.videoanalysissystem.study.consumer;

import dev.videoanalysissystem.study.domain.StudyRecord;
import dev.videoanalysissystem.study.domain.StudyVideoAnalysisProgressStatus;
import dev.videoanalysissystem.study.dto.StudyVideoAnalysisRequest;
import dev.videoanalysissystem.study.dto.StudyVideoAnalysisResponse;
import dev.videoanalysissystem.study.repository.StudyRecordRepository;
import dev.videoanalysissystem.study.repository.StudyVideoAnalysisProgressRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
// 외부 AI 분석 API 서버 로직
public class StudyVideoAnalysisConsumer {

    private final StudyRecordRepository studyRecordRepository;
    // redis cache - 서버 무상태 유지
    private final StudyVideoAnalysisProgressRepository studyAnalysisRepository;

    @Transactional
    @KafkaListener(topics = "study_video_analysis_event", groupId = "group1")
    public void consumeStudyVideoAnalysisEvent(StudyVideoAnalysisRequest request) {
        log.info("request={}", request);
        log.info("execute ai analysis logic={}", request.ofAIRequest());
        // 분석완료큐 & 예외처리 핸들러 필요 - AI API response 를 리턴해주는 컨슈머
        // 아래 리스폰스는 가상의 API response
        StudyVideoAnalysisResponse response = new StudyVideoAnalysisResponse(true, 1, "url");
        String videoURL = request.videoURL();
        StudyRecord studyRecord = studyRecordRepository.findStudyRecordByVideoUrl(videoURL)
                .orElseThrow(IllegalArgumentException::new);

        StudyVideoAnalysisProgressStatus studyVideoAnalysisProgressStatus = studyAnalysisRepository.getOrDefault(
                videoURL);

        // response의 success가 false라면 재시도 큐에 전송 및 status error count 증가
        if (!response.success()) {
            studyVideoAnalysisProgressStatus.increaseErrorCount();
            studyAnalysisRepository.cacheStudyVideoAnalysisProgressStatus(videoURL, studyVideoAnalysisProgressStatus);
            return;
        }

        studyVideoAnalysisProgressStatus.updateStatusProgressing();
        studyVideoAnalysisProgressStatus.increaseStudyMin(response.studyMinute());
        studyAnalysisRepository.cacheStudyVideoAnalysisProgressStatus(videoURL, studyVideoAnalysisProgressStatus);
        // Redis Cache 가 다운될 경우를 대비하여 DB와 동기화
        studyRecord.updateStudyMin(studyVideoAnalysisProgressStatus.getStudyMin());
        if (request.isLastPart()) {
            studyVideoAnalysisProgressStatus.updateStatusCompleted();
            studyVideoAnalysisProgressStatus.increaseStudyMin(response.studyMinute());
            // 마지막 파트 처리시 예외발생한 작업확인 및 처리 로직이 필요하다.
            if (studyVideoAnalysisProgressStatus.hasProgressError()) {

            }
            studyAnalysisRepository.cacheStudyVideoAnalysisProgressStatus(videoURL, studyVideoAnalysisProgressStatus);
            studyRecord.updateStudyMin(studyVideoAnalysisProgressStatus.getStudyMin());
        }
        log.info("study min={}", studyVideoAnalysisProgressStatus.getStudyMin());
    }
}
