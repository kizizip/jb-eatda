package com.jbeatda.DTO.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 전북향토음식점 정보 - 상세정보 조회 응답 결과
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JbStoreDetailApiResponseDTO {

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
        @JsonProperty("item")
        private StoreDetail item;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreDetail {
        @JsonProperty("SNO")
        private String sno;

        @JsonProperty("NAME")
        private String name;

        @JsonProperty("AREA")
        private String area;

        @JsonProperty("ADDRESS")
        private String address;

        @JsonProperty("TEL")
        private String tel;

        @JsonProperty("TIME")
        private String time;

        @JsonProperty("IMG")
        private String img;

        @JsonProperty("SEAT")
        private String seat;

        @JsonProperty("HOLYDAY")
        private String holyday;

        @JsonProperty("PARK")
        private String park;

        @JsonProperty("CAR")
        private String car;

        @JsonProperty("ETC")
        private String etc;

        @JsonProperty("CONTENT")
        private String content;

        @JsonProperty("MAP")
        private String map;

        @JsonProperty("SEQ")
        private String seq;

        @JsonProperty("CKTYPE")
        private String cktype;

        @JsonProperty("FOOD")
        private String food;

        @JsonProperty("TB_STARCOUNT")
        private Integer tbStarcount;

        @JsonProperty("F_LONGITUDE")
        private String fLongitude;

        @JsonProperty("SMENU")
        private String smenu;

        @JsonProperty("TB_STARSCORE")
        private String tbStarscore;

        @JsonProperty("F_LATITUDE")
        private String fLatitude;
    }
}