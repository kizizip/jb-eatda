package com.jbeatda.domain.menus.service;

import com.jbeatda.DTO.responseDTO.MenuResponseDTO;
import com.jbeatda.domain.menus.repository.MenuRepository;
import com.jbeatda.exception.ApiResponseCode;
import com.jbeatda.exception.ApiResponseDTO;
import com.jbeatda.exception.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuRepository menuRepository;

    public ApiResult getMenusByAreaId(Integer areaId) {
        try {
            var menus = menuRepository.findAllByAreaId(areaId);
            if (menus.isEmpty()) {
                return ApiResponseDTO.fail(ApiResponseCode.NO_MENUS_FOUND);
            }
            return MenuResponseDTO.fromList(menus);
        } catch (Exception e) {
            log.error("Menu 조회 실패", e);
            return ApiResponseDTO.fail(ApiResponseCode.SERVER_ERROR);
        }
    }
}
