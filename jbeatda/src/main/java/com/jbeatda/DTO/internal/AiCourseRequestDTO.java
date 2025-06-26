package com.jbeatda.DTO.internal;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder

/**
 * ai 코스 추천받을 데이터 최종 정리
 */
public class AiCourseRequestDTO {
    // 사용자 요구사항
    private List<String> foodStyles;
    private String transportation;
    private String condition;
    private String duration;

    // 매장 정보 리스트
    private List<StoreWithCoordinatesDTO> stores;

    // AI에게 전달할 추가 컨텍스트 (선택사항)
    private String region;
    private Integer maxStoreCount;

}
