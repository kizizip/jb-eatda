package com.jbeatda.DTO.responseDTO;

import com.jbeatda.domain.stamps.entity.Stamp;
import com.jbeatda.exception.ApiResult;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StampResponseDTO implements ApiResult {

    // 여러 스탬프 정보를 담는 리스트
    private List<StampDTO> stamps;

    // 스탬프 1개 정보를 담는 내부 클래스
    @Getter
    @Builder
    public static class StampDTO implements ApiResult{
        private Integer stampId;
        private Integer menuId;
        private String image;
        private String createdAt;
    }

    // 스탬프 엔티티 리스트를 DTO 리스트로 변환하는 메서드
    // 여러 스탬프 정보를 한 번에 변환할 때 사용함
    public static StampResponseDTO fromEntityList(List<Stamp> stamps) {
        List<StampDTO> stampDTOS = stamps.stream()
                .map(stamp -> {
                    return StampDTO.builder()
                            .stampId(stamp.getId())
                            .menuId(stamp.getMenu().getId())
                            .image(stamp.getImage())
                            .createdAt(stamp.getCreatedAt().toString())
                            .build();
                })
                .collect(Collectors.toList());

        return new StampResponseDTO(stampDTOS);
    }

    public static StampDTO fromEntity(Stamp stamp) {
        return StampDTO.builder()
                .stampId(stamp.getId())
                .menuId(stamp.getMenu().getId())
                .image(stamp.getImage())
                .createdAt(stamp.getCreatedAt().toString())
                .build();
    }
}
