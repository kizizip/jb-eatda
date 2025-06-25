package com.jbeatda.Mapper;

import com.jbeatda.DTO.external.DoStoreListApiResponseDTO;
import com.jbeatda.DTO.external.JbStoreListApiResponseDTO;
import com.jbeatda.DTO.responseDTO.DoStoreListResponseDTO;
import com.jbeatda.DTO.responseDTO.JbStoreListResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
/**
 * 도지정향토음식점, 전북향토음식점 - 목록 api 에서 받아온 응답을 dto로 변환
 */
public class StoreMapper {

    //  DoStore 매핑 메서드
    public DoStoreListResponseDTO toDoStoreListResponse(String area, List<DoStoreListApiResponseDTO.StoreItem> apiItems) {
        // Area 정보 생성
        DoStoreListResponseDTO.AreaInfo areaInfo = createAreaInfo(area);

        // Store 목록 변환
        List<DoStoreListResponseDTO.StoreInfo> stores = apiItems.stream()
                .map(this::toStoreInfo)
                .collect(Collectors.toList());

        // Pagination 정보 생성
        DoStoreListResponseDTO.Pagination pagination = createPagination(stores.size());

        return DoStoreListResponseDTO.builder()
                .areaInfo(areaInfo)
                .stores(stores)
                .pagination(pagination)
                .build();
    }

    //  JbStore 매핑 메서드
    public JbStoreListResponseDTO toJbStoreListResponse(List<JbStoreListApiResponseDTO.StoreItem> apiItems) {
        // Store 목록 변환
        List<JbStoreListResponseDTO.StoreInfo> stores = apiItems.stream()
                .map(this::toJbStoreInfo)
                .collect(Collectors.toList());

        // Pagination 정보 생성
        JbStoreListResponseDTO.Pagination pagination = createJbPagination(stores.size());

        JbStoreListResponseDTO response = new JbStoreListResponseDTO();
        response.setStores(stores);
        response.setPagination(pagination);

        return response;
    }

    //  DoStore 변환 메서드
    private DoStoreListResponseDTO.StoreInfo toStoreInfo(DoStoreListApiResponseDTO.StoreItem apiItem) {
        return DoStoreListResponseDTO.StoreInfo.builder()
                .storeId(generateStoreId(apiItem.getSno()))
                .storeName(apiItem.getName())
                .storeImage(apiItem.getImg())
                .address(apiItem.getAddress())
                .smenu(generateDefaultMenu())
                .time(apiItem.getTime())
                .holiday(generateDefaultHoliday())
                .tel(apiItem.getTel())
                .sno(apiItem.getSno())
                .park(generateDefaultPark())
                .build();
    }

    //  JbStore 변환 메서드
    private JbStoreListResponseDTO.StoreInfo toJbStoreInfo(JbStoreListApiResponseDTO.StoreItem apiItem) {
        return JbStoreListResponseDTO.StoreInfo.builder()
                .storeId(generateStoreId(apiItem.getSno()))
                .storeName(apiItem.getName())
                .storeImage(buildImageUrl(apiItem.getImg()))
                .area(apiItem.getArea())
                .address(apiItem.getAddress())
                .smenu(parseMenu(apiItem.getSmenu()))
                .time(apiItem.getTime())
                .holiday(generateJbDefaultHoliday()) // JB API는 휴일 정보가 없음
                .tel(apiItem.getTel())
                .sno(apiItem.getSno())
                .park(generateJbDefaultPark()) // JB API는 주차 정보가 없음
                .build();
    }


    private DoStoreListResponseDTO.AreaInfo createAreaInfo(String areaCode) {
        return DoStoreListResponseDTO.AreaInfo.builder()
                .areaId(areaCode)
                .areaName(getAreaName(areaCode))
                .build();
    }

    private DoStoreListResponseDTO.Pagination createPagination(int totalCount) {
        int pageSize = 10;
        int totalPages = (totalCount + pageSize - 1) / pageSize;

        return DoStoreListResponseDTO.Pagination.builder()
                .currentPage(1)
                .totalPages(Math.max(totalPages, 1))
                .totalCount(totalCount)
                .pageSize(pageSize)
                .hasNext(false)
                .hasPrev(false)
                .build();
    }

    //  JB 페이지네이션 생성
    private JbStoreListResponseDTO.Pagination createJbPagination(int totalCount) {
        int pageSize = 20; // JB API는 20개씩
        int totalPages = (totalCount + pageSize - 1) / pageSize;

        return JbStoreListResponseDTO.Pagination.builder()
                .currentPage(1)
                .totalPages(Math.max(totalPages, 1))
                .totalCount((long) totalCount)
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

    private String getAreaName(String areaCode) {
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

    // 기존 DoStore 전용 메서드들
    private String generateDefaultMenu() {
        return "전통 향토음식";
    }

    private String generateDefaultHoliday() {
        return "문의 바람";
    }

    private Boolean generateDefaultPark() {
        return null;
    }
}