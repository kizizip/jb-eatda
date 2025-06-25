package com.jbeatda.DTO.responseDTO;

import com.jbeatda.domain.areas.entity.Area;
import com.jbeatda.exception.ApiResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaResponseDTO implements ApiResult {

    // 여러 지역 정보를 담는 리스트
    private List<AreaDTO> areas;

    // 개별 지역 정보를 담는 내부 클래스
    @Getter
    @Builder
    public static class AreaDTO  implements ApiResult{
        private Integer areaId;
        private String areaName;
    }

    // 지역 엔티티 리스트를 DTO 리스트로 변환하는 메서드
    // 여러 지역 정보를 한 번에 변환할 때 사용함.
    public static AreaResponseDTO fromList(List<Area> areas){
        List<AreaDTO> areaDTOs = areas.stream()
                .map( area ->{
                    // 각 지역 엔티티를 DTO로 변환
                    return AreaDTO.builder()
                            .areaId(area.getId())
                            .areaName(area.getAreaName())
                            .build();
                        })
                .collect(Collectors.toList());

        return new AreaResponseDTO(areaDTOs);
    }
}
