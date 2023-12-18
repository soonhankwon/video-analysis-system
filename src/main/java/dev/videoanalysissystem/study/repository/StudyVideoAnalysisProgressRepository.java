package dev.videoanalysissystem.study.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.videoanalysissystem.study.domain.StudyRecord;
import dev.videoanalysissystem.study.domain.StudyVideoAnalysisProgressStatus;
import dev.videoanalysissystem.study.domain.StudyVideoAnalysisStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudyVideoAnalysisProgressRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final StudyRecordRepository studyRecordRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StudyVideoAnalysisProgressStatus getOrDefault(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            StudyVideoAnalysisProgressStatus studyVideoAnalysisProgressStatus = null;
            try {
                studyVideoAnalysisProgressStatus = objectMapper.readValue(value,
                        StudyVideoAnalysisProgressStatus.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return studyVideoAnalysisProgressStatus;
        }
        try {
            StudyRecord studyRecord = studyRecordRepository.findStudyRecordByVideoUrl(key)
                    .orElseThrow(IllegalArgumentException::new);
            Integer studyMin = studyRecord.getStudyMin();
            if (studyMin == null) {
                studyMin = 0;
            }
            StudyVideoAnalysisProgressStatus studyVideoAnalysisProgressStatus = new StudyVideoAnalysisProgressStatus(
                    studyMin,
                    StudyVideoAnalysisStatus.READY,
                    0);
            String defaultValue = objectMapper.writeValueAsString(studyVideoAnalysisProgressStatus);
            redisTemplate.opsForValue().set(key, defaultValue);
            return studyVideoAnalysisProgressStatus;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void cacheStudyVideoAnalysisProgressStatus(String videoURL, StudyVideoAnalysisProgressStatus status) {
        try {
            String value = objectMapper.writeValueAsString(status);
            redisTemplate.opsForValue()
                    .set(videoURL, value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
