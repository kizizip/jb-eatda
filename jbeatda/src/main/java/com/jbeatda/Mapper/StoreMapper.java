package com.jbeatda.Mapper;

import com.jbeatda.DTO.external.JbStoreListApiResponseDTO;
import com.jbeatda.DTO.responseDTO.JbListResponseDTO;
import com.jbeatda.DTO.responseDTO.JbSearchListResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
/**
 * 전북향토음식점 - 목록 api 에서 받아온 응답을 dto로 변환
 */
public class StoreMapper {

    /**
     * JbStore 목록 매핑 메서드 (지역별/검색 공통 사용)
     * @param area 지역명
     * @param apiItems JB API 응답 아이템들
     * @return JbListResponseDTO
     */
    public JbListResponseDTO toJbAreaListResponse(String area, List<JbStoreListApiResponseDTO.StoreItem> apiItems) {
        // Area 정보 생성 (area가 null이면 기본값 사용)
        JbListResponseDTO.AreaInfo areaInfo = createJbAreaInfo(area);

        // Store 목록 변환
        List<JbListResponseDTO.StoreInfo> stores = apiItems.stream()
                .map(this::toJbStoreInfo)
                .collect(Collectors.toList());

        // Pagination 정보 생성
        JbListResponseDTO.Pagination pagination = createJbPagination(stores.size());

        return JbListResponseDTO.builder()
                .areaInfo(areaInfo)
                .stores(stores)
                .pagination(pagination)
                .build();
    }

    /**
     * JbStore 매장 정보 변환 메서드 (공통)
     */
    private JbListResponseDTO.StoreInfo toJbStoreInfo(JbStoreListApiResponseDTO.StoreItem apiItem) {
        return JbListResponseDTO.StoreInfo.builder()
                .storeId(generateStoreId(apiItem.getSno()))
                .storeName(apiItem.getName())
                .storeImage(buildImageUrl(apiItem.getImg()))
                .address(apiItem.getAddress())
                .smenu(parseMenu(apiItem.getSmenu()))
                .time(apiItem.getTime())
                .holiday(generateJbDefaultHoliday())
                .tel(apiItem.getTel())
                .sno(apiItem.getSno())
                .park(generateJbDefaultPark())
                .build();
    }

    /**
     * JB 지역 정보 생성
     */
    private JbListResponseDTO.AreaInfo createJbAreaInfo(String area) {
        if (area == null || area.trim().isEmpty()) {
            // 검색 시 지역 정보가 없는 경우
            return JbListResponseDTO.AreaInfo.builder()
                    .areaId("00")
                    .areaName("전체")
                    .build();
        }

        return JbListResponseDTO.AreaInfo.builder()
                .areaId(area)  // 지역코드 ("01", "02" 등)
                .areaName(getAreaName(area))  // 지역명 ("고창군", "군산시" 등)
                .build();
    }

    /**
     * JB 페이지네이션 생성 (통합)
     */
    private JbListResponseDTO.Pagination createJbPagination(int totalCount) {
        int pageSize = 20; // 통일된 페이지 크기
        int totalPages = (totalCount + pageSize - 1) / pageSize;

        return JbListResponseDTO.Pagination.builder()
                .currentPage(1)
                .totalPages(Math.max(totalPages, 1))
                .totalCount(totalCount)
                .pageSize(pageSize)
                .hasNext(false)
                .hasPrev(false)
                .build();
    }

    // 유틸리티 메서드들
    private Long generateStoreId(String sno) {
        try {
            return Long.parseLong(sno);
        } catch (NumberFormatException e) {
            return (long) sno.hashCode();
        }
    }

    /**
     * 지역코드를 지역명으로 변환
     */
    private String getAreaName(String areaCode) {
        if (areaCode == null) return "전체";

        switch (areaCode) {
            case "01": return "고창군";
            case "02": return "군산시";
            case "03": return "김제시";
            case "04": return "남원시";
            case "05": return "무주군";
            case "06": return "부안군";
            case "07": return "순창군";
            case "08": return "완주군";
            case "09": return "익산시";
            case "10": return "임실군";
            case "11": return "장수군";
            case "12": return "전주시";
            case "13": return "정읍시";
            case "14": return "진안군";
            default: return "전라북도";
        }
    }


    // JB API 전용 메서드들
    private String buildImageUrl(String imageName) {
        if (imageName == null || imageName.trim().isEmpty() || "null".equals(imageName)) {
            return null;
        }
        // JB API 이미지 베이스 URL과 조합
        return "http://jbfood.go.kr/datafloder/foodimg/" + imageName.split("\\|")[0];
    }

    private String parseMenu(String menuString) {
        if (menuString == null || menuString.trim().isEmpty()) {
            return "메뉴 정보 없음";
        }
        // ^나 | 등의 구분자를 콤마로 변경
        return menuString.replace("^", ", ").replace("|", ", ");
    }

    private String generateJbDefaultHoliday() {
        return "매장에 문의";
    }

    private Boolean generateJbDefaultPark() {
        return null; // 주차 정보가 없으므로 null
    }
}