package com.jbeatda.Mapper;


import com.jbeatda.DTO.external.DoStoreListApiResponseDTO;
import com.jbeatda.DTO.responseDTO.StoreListResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
/**
 *  도지정향토음식점- 목록 api 에서 받아온 응답을 dto로 변환
 */
public class StoreMapper {

    public StoreListResponseDTO toStoreListResponse(String area, List<DoStoreListApiResponseDTO.StoreItem> apiItems) {
        // Area 정보 생성
        StoreListResponseDTO.AreaInfo areaInfo = createAreaInfo(area);

        // Store 목록 변환
        List<StoreListResponseDTO.StoreInfo> stores = apiItems.stream()
                .map(this::toStoreInfo)
                .collect(Collectors.toList());

        // Pagination 정보 생성
        StoreListResponseDTO.Pagination pagination = createPagination(stores.size());

        return StoreListResponseDTO.builder()
                .areaInfo(areaInfo)
                .stores(stores)
                .pagination(pagination)
                .build();
    }

    private StoreListResponseDTO.StoreInfo toStoreInfo(DoStoreListApiResponseDTO.StoreItem apiItem) {
        return StoreListResponseDTO.StoreInfo.builder()
                .storeId(generateStoreId(apiItem.getSno())) // SNO를 기반으로 ID 생성
                .storeName(apiItem.getName())
                .storeImage(apiItem.getImg())
                .address(apiItem.getAddress())
                .smenu(generateDefaultMenu()) // 기본 메뉴 (API에 메뉴 정보가 없음)
                .time(apiItem.getTime())
                .holiday(generateDefaultHoliday()) // 기본 휴일 정보
                .tel(apiItem.getTel())
                .sno(apiItem.getSno())
                .park(generateDefaultPark()) // 기본 주차 정보
                .build();
    }

    private StoreListResponseDTO.AreaInfo createAreaInfo(String areaCode) {
        return StoreListResponseDTO.AreaInfo.builder()
                .areaId(areaCode)
                .areaName(getAreaName(areaCode))
                .build();
    }

    private StoreListResponseDTO.Pagination createPagination(int totalCount) {
        int pageSize = 10;
        int totalPages = (totalCount + pageSize - 1) / pageSize; // 올림 계산

        return StoreListResponseDTO.Pagination.builder()
                .currentPage(1)
                .totalPages(Math.max(totalPages, 1)) // 최소 1페이지
                .totalCount(totalCount)
                .pageSize(pageSize)
                .hasNext(false) // 현재는 페이징 처리 안 함
                .hasPrev(false)
                .build();
    }

    // 유틸리티 메서드들
    private Long generateStoreId(String sno) {
        try {
            return Long.parseLong(sno);
        } catch (NumberFormatException e) {
            return (long) sno.hashCode(); // SNO를 숫자로 변환할 수 없으면 해시코드 사용
        }
    }

    private String getAreaName(String areaCode) {
        // 지역 코드를 지역명으로 변환
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

    private String generateDefaultMenu() {
        // API에서 제공하지 않는 정보이므로 기본값 또는 null
        return "전통 향토음식"; // 또는 null
    }

    private String generateDefaultHoliday() {
        // API에서 제공하지 않는 정보이므로 기본값
        return "문의 바람"; // 또는 null
    }

    private Boolean generateDefaultPark() {
        // API에서 제공하지 않는 정보이므로 기본값
        return null; // 또는 false
    }
}