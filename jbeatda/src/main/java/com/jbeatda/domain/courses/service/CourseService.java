package com.jbeatda.domain.courses.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbeatda.DTO.external.JbStoreListApiResponseDTO;
import com.jbeatda.DTO.internal.AiCourseRequestDTO;
import com.jbeatda.DTO.internal.StoreWithCoordinatesDTO;
import com.jbeatda.DTO.requestDTO.CourseSelectionRequestDTO;
import com.jbeatda.DTO.responseDTO.AiCourseResponseDTO;
import com.jbeatda.domain.stores.client.JbStoreApiClient;
import com.jbeatda.domain.stores.client.KakaoClient;
import com.jbeatda.exception.AiException;
import com.jbeatda.exception.ApiResponseCode;
import com.jbeatda.exception.ApiResult;
import com.jbeatda.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.jbeatda.domain.courses.client.OpenAiClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final JbStoreApiClient jbStoreApiClient;
    private final KakaoClient kakaoClient;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    public ApiResult recommendCourse(CourseSelectionRequestDTO requestDTO){

        // 1. 지역 기준으로 식당 조회 (전북향토음식점목록조회 api)
        List<JbStoreListApiResponseDTO.StoreItem> storeList = new ArrayList<>();

        for(String area: requestDTO.getRegions()){
            List<JbStoreListApiResponseDTO.StoreItem> apiItems = jbStoreApiClient.jbStoreAreaList(area);
            storeList.addAll(apiItems); //
        }

        // 2. 식당 별로 위도 경도 추가하기
        List<StoreWithCoordinatesDTO> storesWithCoordinates = new ArrayList<>();

        for(JbStoreListApiResponseDTO.StoreItem storeItem : storeList) {
            try {
                // 2-1. 카카오 API로 주소 → 좌표 변환
                List<String> coordinates = kakaoClient.getPoint(storeItem.getAddress());
                String latitude = coordinates.get(0);   // 위도
                String longitude = coordinates.get(1);  // 경도

                log.info("카카오 API로 좌표 변환 완료 - 매장: {}, 주소: {}, lat: {}, lng: {}",
                        storeItem.getName(), storeItem.getAddress(), latitude, longitude);

                // 2-2. 좌표가 포함된 매장 정보 생성
                StoreWithCoordinatesDTO storeWithCoords = StoreWithCoordinatesDTO.builder()
                        .storeItem(storeItem)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();

                storesWithCoordinates.add(storeWithCoords);

            } catch (Exception e) {
                log.error("매장 좌표 처리 실패 - 매장: {}, 주소: {}",
                        storeItem.getName(), storeItem.getAddress(), e);
                // 좌표 변환 실패한 매장은 제외하거나, 기본값으로 처리
                continue;
            }
        }

        log.info("좌표 처리 완료 - 총 매장 수: {}, 성공: {}",
                storeList.size(), storesWithCoordinates.size());

        // 3. ai에 보낼 데이터 최종 정리 AiCourseRequestDTO 만들기
        AiCourseRequestDTO aiRequest = AiCourseRequestDTO.builder()
                .foodStyles(requestDTO.getFoodStyles())
                .transportation(requestDTO.getTransportation())
                .condition(requestDTO.getCondition())
                .duration(requestDTO.getDuration())
                .stores(storesWithCoordinates)
                .region(String.join(",", requestDTO.getRegions()))
                .build();

        // 4. AI에게 코스 추천 요청 AiCourseRequestDTO 전달
        String aiRecommendation = openAiClient.recommendCourse(aiRequest);
        log.info("AI 코스 추천 완료");


        // 5. AI 응답을 JSON으로 파싱하여 DTO 변환 AiCourseResponseDTO
        try{
            AiCourseResponseDTO courseResponse = objectMapper.readValue(aiRecommendation, AiCourseResponseDTO.class);
            log.info("AI 응답 파싱 완료 - 코스명: {}, 매장 수: {}",
                    courseResponse.getCourseName(), courseResponse.getStoreCount());

            return courseResponse;


        } catch (Exception e) {
            log.error("코스 추천 처리 중 오류 발생", e);

            // JSON 파싱 실패인 경우
            if (e.getMessage() != null && e.getMessage().contains("JSON")) {
                throw AiException.parseError(e.getMessage());
            }

            // 기타 예외는 ExternalApiException으로 처리
            throw new ExternalApiException("코스 추천 중 오류가 발생했습니다.");
        }
    }
}