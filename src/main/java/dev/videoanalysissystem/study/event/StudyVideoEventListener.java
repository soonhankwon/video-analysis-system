package dev.videoanalysissystem.study.event;

import dev.videoanalysissystem.study.domain.StudyRecord;
import dev.videoanalysissystem.study.dto.StudyVideoAnalysisRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyVideoEventListener {

    private static final String CALL_BACK_URL = "http://localhost:8080/api/v1/study/";
    private static final int FRAME_PAGING_COUNT = 900;
    private final KafkaTemplate<String, StudyVideoAnalysisRequest> kafkaTemplate;

    @Async
    @EventListener
    public void handleStudyVideoAnalysisEvent(StudyVideoAnalysisEvent event) {
        log.info("event on");
        // 이벤트 프레임단위 페이징 처리 및 카프카 메세지큐로 전송 -> 외부 API 요청
        StudyRecord studyRecord = event.studyRecord();
        String videoUrl = studyRecord.getVideoUrl();
        Integer videoFrameCount = studyRecord.getVideoFrameCount();

        // 900프레임으로 videoFrame을 페이징 처리해서 메세지큐로 전송
        int pages = (int) Math.ceil((double) videoFrameCount / FRAME_PAGING_COUNT);
        executePaging(pages, videoFrameCount, videoUrl);
        log.info("current thread={}", Thread.currentThread().isVirtual());
    }

    // 페이징 처리 메서드 - 프레임단위 페이징처리 후 카프카로 이벤트 발행
    // 마지막 파트의 flag - isLastPart = true
    private void executePaging(int pages, Integer videoFrameCount, String videoUrl) {
        long index = 0;
        for (int i = 0; i < pages; i++) {
            if (i == pages - 1) {
                int nowTotalFrameCount = FRAME_PAGING_COUNT * (pages - 1);
                int remainFrameCount = videoFrameCount - nowTotalFrameCount - 1;
                StudyVideoAnalysisRequest studyVideoAnalysisRequest = new StudyVideoAnalysisRequest(CALL_BACK_URL,
                        videoUrl, index, index + remainFrameCount, true);
                sendVideoPartToKafka(studyVideoAnalysisRequest);
                return;
            }
            StudyVideoAnalysisRequest studyVideoAnalysisRequest = new StudyVideoAnalysisRequest(CALL_BACK_URL, videoUrl,
                    index, index + 899L, false);
            sendVideoPartToKafka(studyVideoAnalysisRequest);
            index += 900L;
        }
    }

    private void sendVideoPartToKafka(StudyVideoAnalysisRequest request) {
        log.info("request={}", request);
        String videoUrl = request.videoURL();
        kafkaTemplate.send("study_video_analysis_event", videoUrl, request);
    }
}
