package com.jbeatda.domain.areas.service;

import com.jbeatda.DTO.responseDTO.AreaResponseDTO;
import com.jbeatda.domain.areas.entity.Area;
import com.jbeatda.domain.areas.repository.AreaRepository;
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
public class AreaService {

    private final AreaRepository areaRepository;

    // 지역 목록 전체 조회
    public ApiResult getAllAreas(){
        try{
            List<Area> areas = areaRepository.findAllByOrderByIdAsc();

            if(areas.isEmpty()){
                return ApiResponseDTO.fail(ApiResponseCode.NO_AREAS_FOUND);
            }

            return AreaResponseDTO.fromList(areas);
        } catch (Exception e){
            return ApiResponseDTO.fail(ApiResponseCode.SERVER_ERROR);
        }
    }

    // 지역 목록 단건 조회
    public ApiResult getAreaById(Integer areaId) {
        try{
            Area area = areaRepository.findFirstById(areaId);
            if (area == null) {
                return ApiResponseDTO.fail(ApiResponseCode.NO_AREAS_FOUND);
            }

            return AreaResponseDTO.AreaDTO.builder()
                    .areaId(area.getId())
                    .areaName(area.getAreaName())
                    .build();
            
        } catch (Exception e){
            return ApiResponseDTO.fail(ApiResponseCode.SERVER_ERROR);
        }
    }
}
