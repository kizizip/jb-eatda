package com.jbeatda.DTO.external;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * 전북향토음식점 정보 - 목록조회응답결과
 */
public class JbStoreListApiResponseDTO {
    private Header header;
    private Body body;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private String totalCount;
        private Items items;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        @JsonProperty("item")
        private List<StoreItem> item = new ArrayList<>();  // 항상 List로 받기
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    public static class StoreItem {
        @JsonProperty("IMG")
        private String img;

        @JsonProperty("AREA")
        private String area;

        @JsonProperty("SNO")
        private String sno;

        @JsonProperty("ADDRESS")
        private String address;

        @JsonProperty("TB_STARCOUNT")
        private Integer tbStarcount;

        @JsonProperty("F_LONGITUDE")
        private String fLongitude;

        @JsonProperty("SMENU")
        private String smenu;

        @JsonProperty("TB_STARSCORE")
        private String tbStarscore;  // null 값이 올 수 있어서 String으로 처리

        @JsonProperty("TEL")
        private String tel;

        @JsonProperty("TIME")
        private String time;

        @JsonProperty("F_LATITUDE")
        private String fLatitude;

        @JsonProperty("NAME")
        private String name;
    }

}
