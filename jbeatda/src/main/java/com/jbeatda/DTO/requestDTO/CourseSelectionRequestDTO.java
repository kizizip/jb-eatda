package com.jbeatda.DTO.requestDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CourseSelectionRequestDTO {
    private List<String> regions;
    private List<String> foodStyles;
    private String transportation;
    private String condition;
    private String duration;
}
