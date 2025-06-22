package com.jbeatda.DTO.responseDTO;

import com.jbeatda.domain.stores.entity.Store;
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
public class StoreResponseDTO implements ApiResult {

    // 여러 가게 정보를 담는 리스트
    private List<StoreDTO> stores;

    // 개별 가게 정보를 담는 내부 클래스
    @Getter
    @Builder
    public static class StoreDTO implements ApiResult {
        private Integer storeId;
        private String storeName;
        private String storeImage;
        private String area;
        private String address;
        private String smenu;
        private String time;
        private String holiday;
        private String tel;
        private String sno;
        private Boolean park;
    }

    // 가게 엔티티 리스트를 DTO 리스트로 변환하는 메서드
    // 여러 가게 정보를 한 번에 변환할 때 사용함.
    public static StoreResponseDTO fromList(List<Store> stores) {
        List<StoreDTO> storeDTOs = stores.stream()
                .map(store -> {
                    // 각 가게 엔티티를 DTO로 변환
                    return StoreDTO.builder()
                            .storeId(store.getId())
                            .storeName(store.getStoreName())
                            .storeImage(store.getStoreImage())
                            .area(store.getArea())
                            .address(store.getAddress())
                            .smenu(store.getSmenu())
                            .time(store.getTime())
                            .holiday(store.getHoliday())
                            .tel(store.getTel())
                            .sno(store.getSno())
                            .park(store.getPark())
                            .build();
                })
                .collect(Collectors.toList());

        return new StoreResponseDTO(storeDTOs);
    }
}
