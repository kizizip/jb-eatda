package com.jbeatda.DTO.responseDTO;

import com.jbeatda.exception.ApiResult;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JbListResponseDTO implements ApiResult {
    private AreaInfo areaInfo;
    private List<StoreInfo> stores;
    private Pagination pagination;

    @Data
    @Builder
    public static class AreaInfo {
        private String areaId;
        private String areaName;
    }

    @Data
    @Builder
    public static class StoreInfo {
        private Long storeId;
        private String storeName;
        private String storeImage;
        private String address;
        private String smenu;
        private String time;
        private String holiday;
        private String tel;
        private String sno;
        private Boolean park;
    }

    @Data
    @Builder
    public static class Pagination {
        private Integer currentPage;
        private Integer totalPages;
        private Integer totalCount;
        private Integer pageSize;
        private Boolean hasNext;
        private Boolean hasPrev;
    }
}