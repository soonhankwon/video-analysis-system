package dev.videoanalysissystem.study.dto;

public record StudyVideoAnalysisResponse(
        //성공 여부
        boolean success,
        //분석 결과 공부 시간 (분 단위)
        Integer studyMinute,
        //상세 분석 결과 파일 url
        String detailResultFileUrl
) {
}
