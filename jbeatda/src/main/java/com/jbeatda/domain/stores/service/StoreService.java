package com.jbeatda.domain.stores.service;

import com.jbeatda.DTO.responseDTO.StoreResponseDTO;
import com.jbeatda.domain.stores.entity.Store;
import com.jbeatda.domain.stores.repository.StoreRepository;
import com.jbeatda.exception.ApiResponseCode;
import com.jbeatda.exception.ApiResponseDTO;
import com.jbeatda.exception.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;

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
}
