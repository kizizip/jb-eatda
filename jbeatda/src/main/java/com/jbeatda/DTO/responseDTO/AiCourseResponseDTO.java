package com.jbeatda.DTO.responseDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jbeatda.exception.ApiResult;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiCourseResponseDTO implements ApiResult {
    private String courseName;
    private String description;
    private Integer storeCount;
    private List<RecommendedStore> stores;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RecommendedStore {
        private String storeName;
        private String storeImage;
        private String area;
        private String address;
        private String smenu;
        private String time;
        private String tel;
        private String sno;
        private Integer visitOrder;
        private String lat;   // 위도
        private String lng;   // 경도
    }
}