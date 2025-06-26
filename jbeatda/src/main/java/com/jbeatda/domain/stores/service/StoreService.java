package com.jbeatda.domain.stores.service;

import com.jbeatda.DTO.external.JbStoreDetailApiResponseDTO;
import com.jbeatda.DTO.external.JbStoreListApiResponseDTO;
import com.jbeatda.DTO.requestDTO.SearchStoreRequestDTO;
import com.jbeatda.DTO.responseDTO.JbListResponseDTO;
import com.jbeatda.DTO.responseDTO.StoreResponseDTO;
import com.jbeatda.Mapper.StoreDetailMapper;
import com.jbeatda.Mapper.StoreMapper;
import com.jbeatda.domain.stores.client.DoStoreApiClient;
import com.jbeatda.domain.stores.client.JbStoreApiClient;
import com.jbeatda.domain.stores.client.KakaoClient;
import com.jbeatda.domain.stores.entity.Store;
import com.jbeatda.domain.stores.repository.StoreRepository;
import com.jbeatda.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;
    private final DoStoreApiClient doStoreApiClient;
    private final StoreMapper storeMapper;
    private final KakaoClient kakaoClient;
    private final StoreDetailMapper storeDetailMapper;
    private final JbStoreApiClient jbStoreApiClient;

    // 전체 가게 목록 조회
    public ApiResult getAllStores() {
        try {
            List<Store> stores = storeRepository.findAllByOrderByIdAsc();

            if (stores.isEmpty()) {
                return ApiResponseDTO.fail(ApiResponseCode.NO_STORES_FOUND);
            }

            return StoreResponseDTO.fromList(stores);
        } catch (Exception e) {
            return ApiResponseDTO.fail(ApiResponseCode.SERVER_ERROR);
        }
    }

    // 특정 지역의 식당 목록 조회
    public ApiResult getStoresByArea(String area) {

        // 1. 외부 API 호출
        List<JbStoreListApiResponseDTO.StoreItem> apiItems = jbStoreApiClient.jbStoreAreaList(area);
        // 2. DTO 변환
        JbListResponseDTO response = storeMapper.toJbAreaListResponse(area, apiItems);
        // 3. 반환
        return response ;
    }

    // 식당검색 (지역번호 + 키워드)
    public ApiResult searchStore(SearchStoreRequestDTO searchStoreRequestDTO){

        // 1. 외부 API 호출
        List<JbStoreListApiResponseDTO.StoreItem> apiItems = jbStoreApiClient.jbStoreSearchList(searchStoreRequestDTO);
        // 2. DTO 변환
        JbListResponseDTO response = storeMapper.toJbAreaListResponse(searchStoreRequestDTO.getArea(), apiItems);
        // 3. 반환
        return response ;

    }

    // 식당 정보 상세 조회
    public ApiResult getStoresDetail(int storeId) {
        String store = String.valueOf(storeId);
        // 1. JB API 호출
        JbStoreDetailApiResponseDTO.StoreDetail apiItem = jbStoreApiClient.jbStoreDetail(store);

        // 2. 경도, 위도 받아오기
        List<String> point = kakaoClient.getPoint(apiItem.getAddress());

        // 3. DTO 변환
        return storeDetailMapper.toJbStoreDetailResponse(apiItem, point);

    }


}
