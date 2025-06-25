package com.jbeatda.DTO.responseDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jbeatda.exception.ApiResult;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * 전북향토음식점 목록 조회 응답 DTO
 */
public class JbStoreListResponseDTO implements ApiResult {

    private List<StoreInfo> stores = new ArrayList<>();
    private Pagination pagination;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    public static class StoreInfo {
        private Long storeId;
        private String storeName;
        private String storeImage;
        private String area;
        private String address;
        private String smenu;
        private String time;
        private String holiday;
        private String tel;
        private String sno;
        private Boolean park;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    public static class Pagination {
        private Integer currentPage;
        private Integer totalPages;
        private Long totalCount;
        private Integer pageSize;
        private Boolean hasNext;
        private Boolean hasPrev;
    }
}