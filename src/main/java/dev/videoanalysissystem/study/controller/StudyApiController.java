package dev.videoanalysissystem.study.controller;

import dev.videoanalysissystem.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/study")
public class StudyApiController {

    private final StudyService studyService;

    @PostMapping("/analysis/{videoId}")
    public ResponseEntity<String> analysisStudyVideo(@PathVariable Long videoId) {
        String videoUrl = studyService.analysisStudyVideo(videoId);
        return ResponseEntity.ok().body("분석 요청 성공." + videoUrl);
    }

    //callback url - 동영상 분석 완료 체크 (분석 완료 이벤트 큐 역할)
    @GetMapping("{videoUrl}")
    public ResponseEntity<String> getStudyAnalysisStatus(@PathVariable String videoUrl) {
        String studyAnalysisStatus = studyService.getStudyAnalysisStatus(videoUrl);
        if (studyAnalysisStatus.equals("분석 완료")) {
            return ResponseEntity.ok().body(studyAnalysisStatus);
        }
        if (studyAnalysisStatus.equals("분석이 종료됬지만, 예외처리 중 입니다.")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(studyAnalysisStatus);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(studyAnalysisStatus);
    }
}
