package com.jbeatda.DTO.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateCourseRequestDTO {

    @NotBlank(message = "코스명은 필수입니다")
    @JsonProperty("courseName")
    private String courseName;

    @NotBlank(message = "코스 설명은 필수입니다")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "매장 수는 필수입니다")
    @Min(value = 1, message = "매장 수는 1개 이상이어야 합니다")
    @JsonProperty("storeCount")
    private Integer storeCount;

    @NotEmpty(message = "매장 목록은 필수입니다")
    @JsonProperty("stores")
    private List<StoreDTO> stores;

    @Data
    @NoArgsConstructor
    public static class StoreDTO {


        @NotBlank(message = "식당번호은 필수입니다")
        @JsonProperty("sno")
        private String sno;

        @NotBlank(message = "매장명은 필수입니다")
        @JsonProperty("storeName")
        private String storeName;

        @JsonProperty("storeImage")
        private String storeImage;

        @NotBlank(message = "지역은 필수입니다")
        @JsonProperty("area")
        private String area;

        @NotBlank(message = "주소는 필수입니다")
        @JsonProperty("address")
        private String address;

        @NotBlank(message = "대표 메뉴는 필수입니다")
        @JsonProperty("smenu")
        private String smenu;

        @NotBlank(message = "영업시간은 필수입니다")
        @JsonProperty("time")
        private String time;

        @JsonProperty("tel")
        private String tel;

        @NotNull(message = "방문 순서는 필수입니다")
        @Min(value = 1, message = "방문 순서는 1 이상이어야 합니다")
        @JsonProperty("visitOrder")
        private Integer visitOrder;

        @NotNull(message = "위도는 필수입니다")
        @JsonProperty("lat")
        private String lat;

        @NotNull(message = "경도는 필수입니다")
        @JsonProperty("lng")
        private String lng;
    }
}