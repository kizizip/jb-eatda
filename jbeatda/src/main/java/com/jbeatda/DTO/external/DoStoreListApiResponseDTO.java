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
 * 도지정향토음식점 정보 - 목록조회응답결과
 */
public class DoStoreListApiResponseDTO {

    private Header header;
    private Body body;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header{
        private String resultCode;
        private String resultMsg;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body{
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
    public static class StoreItem{
        @JsonProperty("IMG")
        private String img;

        @JsonProperty("AREA")
        private String area;

        @JsonProperty("SNO")
        private String sno;

        @JsonProperty("ADDRESS")
        private String address;

        @JsonProperty("TEL")
        private String tel;

        @JsonProperty("TIME")
        private String time;

        @JsonProperty("NAME")
        private String name;


    }



}
