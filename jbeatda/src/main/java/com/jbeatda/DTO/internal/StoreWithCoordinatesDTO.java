package com.jbeatda.DTO.internal;

import com.jbeatda.DTO.external.JbStoreListApiResponseDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * ai에 보낼 최종 식당 리스트
 */
@Getter
@Builder
@Setter
public class StoreWithCoordinatesDTO {
    private JbStoreListApiResponseDTO.StoreItem storeItem;
    private String latitude;   // 위도
    private String longitude;  // 경도

    // AI에게 전달할 때 사용할 간단한 정보 추출 메서드
    public String getStoreName() {
        return storeItem.getName();
    }

    public String getAddress() {
        return storeItem.getAddress();
    }

    public String getMenu() {
        return storeItem.getSmenu();
    }
}

