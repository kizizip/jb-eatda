package com.jbeatda.DTO.responseDTO;

import com.jbeatda.exception.ApiResult;
import lombok.*;

import java.util.List;

@Setter
@Getter
public class CourseListResponseDTO implements ApiResult {

    private List<MyCourse> courses;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MyCourse {
        private int courseId;
        private String  courseName;
        private String description;
        private List<String> position;

    }


}
