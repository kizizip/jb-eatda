package com.jbeatda.Mapper;

import com.jbeatda.DTO.external.JbStoreDetailApiResponseDTO;
import com.jbeatda.DTO.responseDTO.StoreDetailResponseDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 전북향토음식점 - 상세 api 에서 받아온 응답을 dto로 변환
 */
@Service
@Slf4j
@AllArgsConstructor
public class StoreDetailMapper {

    /**
     * JbStore API 응답 + Kakao API 좌표를 최종 응답 DTO로 매핑
     */
    public StoreDetailResponseDTO toJbStoreDetailResponse(
            JbStoreDetailApiResponseDTO.StoreDetail storeDetail,
            List<String> coordinates) {

        if (storeDetail == null) {
            log.warn("JbStoreDetail이 null입니다.");
            return null;
        }

        return StoreDetailResponseDTO.builder()
                .storeName(storeDetail.getName())
                .storeImage(buildJbImageUrl(storeDetail.getImg()))
                .area(storeDetail.getArea())
                .address(storeDetail.getAddress())
                .smenu(parseJbMenu(storeDetail.getFood(), storeDetail.getSmenu()))
                .time(storeDetail.getTime())
                .holyday(storeDetail.getHolyday())
                .tel(storeDetail.getTel())
                .sno(storeDetail.getSno())
                .park(convertParkFlag(storeDetail.getPark()))
                .seat(Integer.parseInt(storeDetail.getSeat()))
                .lat(extractLatitude(coordinates))
                .lng(extractLongitude(coordinates))
                .build();
    }

    /**
     * JbStore API 응답만으로 DTO 생성 (좌표 없이)
     */
    public StoreDetailResponseDTO toJbStoreDetailResponse(JbStoreDetailApiResponseDTO.StoreDetail storeDetail) {
        return toJbStoreDetailResponse(storeDetail, null);
    }

    // 유틸리티 메서드들
    private boolean convertParkFlag(String parkFlag) {
        if (parkFlag == null || parkFlag.trim().isEmpty()) {
            return false;
        }

        String normalized = parkFlag.trim().toLowerCase();
        return "y".equals(normalized) || "yes".equals(normalized);
    }

    private String extractLatitude(List<String> coordinates) {
        if (coordinates != null && coordinates.size() >= 2) {
            String latitude = coordinates.get(0);
            log.debug("위도 추출 완료: {}", latitude);
            return latitude;
        }
        log.warn("좌표 정보에서 위도를 추출할 수 없습니다.");
        return null;
    }

    private String extractLongitude(List<String> coordinates) {
        if (coordinates != null && coordinates.size() >= 2) {
            String longitude = coordinates.get(1);
            log.debug("경도 추출 완료: {}", longitude);
            return longitude;
        }
        log.warn("좌표 정보에서 경도를 추출할 수 없습니다.");
        return null;
    }

    // JB API 전용 유틸리티 메서드들
    private String buildJbImageUrl(String imageName) {
        if (imageName == null || imageName.trim().isEmpty() || "null".equals(imageName)) {
            return null;
        }
        // JB API 이미지 베이스 URL과 조합 (파이프 구분자로 분리된 첫 번째 값 사용)
        String fileName = imageName.split("\\|")[0];
        return "http://jbfood.go.kr/datafloder/foodimg/" + fileName;
    }

    private String parseJbMenu(String food, String smenu) {
        // FOOD 필드를 우선 사용, 없으면 SMENU 사용
        String menuText = null;

        if (food != null && !food.trim().isEmpty() && !"null".equals(food.trim())) {
            menuText = food;
        } else if (smenu != null && !smenu.trim().isEmpty() && !"null".equals(smenu.trim())) {
            menuText = smenu;
        }

        // null 체크 및 변환 처리
        if (menuText != null) {
            return menuText.replace("^", ", ").replace("|", ", ");
        }

        return "메뉴 정보 없음";
    }
}