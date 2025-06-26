package com.jbeatda.DTO.responseDTO;

import com.jbeatda.exception.ApiResult;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CourseDetailResponseDTO implements ApiResult {

    private int courseId;
    private String courseName;
    private String description;
    private int storeCount;
    private List<storeList> stores;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class storeList {
        private int storeId;
        private String storeName;
        private String address;
        private String smenu;
        private int visitOrder;
        private String lat;
        private String lng;


    }
}
