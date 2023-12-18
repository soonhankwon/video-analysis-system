package dev.videoanalysissystem.study.repository;

import dev.videoanalysissystem.study.domain.StudyRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {
    Optional<StudyRecord> findStudyRecordByVideoUrl(String videoUrl);
}
