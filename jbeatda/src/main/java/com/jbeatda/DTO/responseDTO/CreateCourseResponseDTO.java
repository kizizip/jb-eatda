package com.jbeatda.DTO.responseDTO;

import com.jbeatda.exception.ApiResult;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseResponseDTO implements ApiResult {
    private Integer courseId;

    public static CreateCourseResponseDTO createDTO(int courseId){
        return CreateCourseResponseDTO.builder()
                .courseId(courseId)
                .build();
    }
}

