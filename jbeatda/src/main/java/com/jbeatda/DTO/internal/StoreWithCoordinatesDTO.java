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

    // AI에게 전달할 때 사용할 정보 추출 메서드들
    public String getStoreName() {
        return storeItem != null ? storeItem.getName() : "";
    }

    public String getSno() {
        return storeItem != null ? storeItem.getSno() : "";
    }

    public String getStoreImage() {
        if (storeItem == null || storeItem.getImg() == null) return null;
        // JB API 이미지 URL 생성
        String imageName = storeItem.getImg().split("\\|")[0];
        return "http://jbfood.go.kr/datafloder/foodimg/" + imageName;
    }

    public String getArea() {
        return storeItem != null ? storeItem.getArea() : "";
    }

    public String getAddress() {
        return storeItem != null ? storeItem.getAddress() : "";
    }

    public String getMenu() {
        if (storeItem == null || storeItem.getSmenu() == null) return "";
        // 메뉴 구분자 정리
        return storeItem.getSmenu().replace("^", ", ").replace("|", ", ");
    }

    public String getTime() {
        return storeItem != null ? storeItem.getTime() : "";
    }

    public String getHoliday() {
        return "매장에 문의"; // JB API에는 휴무일 정보가 없음
    }

    public String getTel() {
        return storeItem != null ? storeItem.getTel() : "";
    }


    public String getSeats() {
        return "문의"; // JB API에는 좌석 정보가 없음
    }
}