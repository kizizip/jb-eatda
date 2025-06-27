package com.jbeatda.domain.courses.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbeatda.DTO.external.JbStoreDetailApiResponseDTO;
import com.jbeatda.DTO.external.JbStoreListApiResponseDTO;
import com.jbeatda.DTO.internal.AiCourseRequestDTO;
import com.jbeatda.DTO.internal.StoreWithCoordinatesDTO;
import com.jbeatda.DTO.requestDTO.CourseSelectionRequestDTO;
import com.jbeatda.DTO.requestDTO.CreateCourseRequestDTO;
import com.jbeatda.DTO.responseDTO.*;
import com.jbeatda.domain.courses.entity.Course;
import com.jbeatda.domain.courses.entity.CourseStore;
import com.jbeatda.domain.courses.repository.CourseRepository;
import com.jbeatda.domain.courses.repository.CourseStoreRepository;
import com.jbeatda.domain.stores.client.JbStoreApiClient;
import com.jbeatda.domain.stores.client.KakaoClient;
import com.jbeatda.domain.stores.entity.Store;
import com.jbeatda.domain.stores.repository.StoreRepository;
import com.jbeatda.domain.users.entity.User;
import com.jbeatda.domain.users.repository.UserRepository;
import com.jbeatda.exception.AiException;
import com.jbeatda.exception.ApiResponseCode;
import com.jbeatda.exception.ApiResult;
import com.jbeatda.exception.ExternalApiException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.jbeatda.domain.courses.client.OpenAiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final JbStoreApiClient jbStoreApiClient;
    private final KakaoClient kakaoClient;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final StoreRepository storeRepository;
    private final CourseStoreRepository courseStoreRepository;


    // ai 코스 추천
    public ApiResult recommendCourse(CourseSelectionRequestDTO requestDTO) {

        // 1. 지역 기준으로 식당 조회 (전북향토음식점목록조회 api)
        List<JbStoreListApiResponseDTO.StoreItem> storeList = new ArrayList<>();

        for (String area : requestDTO.getRegions()) {
            List<JbStoreListApiResponseDTO.StoreItem> apiItems = jbStoreApiClient.jbStoreAreaList(area);
            storeList.addAll(apiItems); //
        }

        // 2. 식당 별로 위도 경도 추가하기
        List<StoreWithCoordinatesDTO> storesWithCoordinates = new ArrayList<>();

        for (JbStoreListApiResponseDTO.StoreItem storeItem : storeList) {
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
        try {
            AiCourseResponseDTO courseResponse = objectMapper.readValue(aiRecommendation, AiCourseResponseDTO.class);
            log.info("AI 응답 파싱 완료 - 코스명: {}, 매장 수: {}",
                    courseResponse.getCourseName(), courseResponse.getStoreCount());

            // 매장 수가 3개를 초과하면 3개로 제한
            if (courseResponse.getStores() != null && courseResponse.getStores().size() > 3) {
                log.info("매장 수 제한 적용 - 기존: {}개 → 제한: 3개", courseResponse.getStores().size());

                // visitOrder 기준으로 정렬 후 상위 3개만 선택
                List<AiCourseResponseDTO.RecommendedStore> limitedStores = courseResponse.getStores().stream()
                        .sorted((s1, s2) -> Integer.compare(s1.getVisitOrder(), s2.getVisitOrder()))
                        .limit(3)
                        .collect(Collectors.toList());

                courseResponse.setStores(limitedStores);
                courseResponse.setStoreCount(3);
            }

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

    //코스 생성 및 저장
    @Transactional
    public ApiResult createCourse(int userId, CreateCourseRequestDTO requestDTO) {

        // 1. 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        // 2. Course 엔티티 생성 및 저장
        Course course = Course.fromBaseUser(user, requestDTO);
        Course savedCourse = courseRepository.save(course);

        // 3. 각 Store 정보 처리 및 CourseStore 생성
        List<CourseStore> courseStores = new ArrayList<>();
        for (CreateCourseRequestDTO.StoreDTO storeDTO : requestDTO.getStores()) {

            // 3-1. Store 존재 여부 확인
            Store store = storeRepository.findBySno(storeDTO.getSno())
                    .orElseGet(() -> {
                        // 3-2. Store가 없으면 공공 API 호출해서 상세 정보 가져오기
                        log.info("Store 정보가 없어서 공공 API 호출 - sno: {}", storeDTO.getSno());

                        JbStoreDetailApiResponseDTO.StoreDetail storeDetail =
                                jbStoreApiClient.jbStoreDetail(storeDTO.getSno());

                        if (storeDetail == null) {
                            log.warn("공공 API에서 매장 정보를 찾을 수 없음 - sno: {}", storeDTO.getSno());
                            throw new EntityNotFoundException("매장 정보를 찾을 수 없습니다. SNO: " + storeDTO.getSno());
                        }

                        // 3-3. 클라이언트 제공 좌표 사용
                        List<String> coordinates = List.of(storeDTO.getLat(), storeDTO.getLng());
                        log.info("클라이언트 제공 좌표 사용 - coordinates: {}", coordinates);

                        // 3-4. StoreDetail을 Store 엔티티로 직접 변환 및 저장
                        Store newStore = Store.fromStoreDetail(storeDetail, coordinates);

                        return storeRepository.save(newStore);
                    });

            // 3-5. CourseStore 생성 (팩토리 메서드 사용)
            CourseStore courseStore = CourseStore.fromBase(
                    savedCourse,
                    store,
                    storeDTO.getVisitOrder()
            );

            courseStores.add(courseStore);
        }

        // 4. CourseStore 일괄 저장
        courseStoreRepository.saveAll(courseStores);

        // 5. Course에 CourseStore 리스트 설정 (양방향 관계 동기화)
        savedCourse.setCourseStores(courseStores);

        // 6. 성공 응답 반환
        return CreateCourseResponseDTO.createDTO(savedCourse.getId());
    }


    // 특정 사용자의 전체 코스 목록
    public ApiResult getCourseList(int userId){

        // 1. 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        // 2. 해당 유저의 코스 가져오기
        List<Course> courseList =  courseRepository.findByUserIdWithStores(user.getId());

        // 3. 유저의 코스와 관련된 식당들의 도시 이름 담기 set
        List<CourseListResponseDTO.MyCourse> myCourses = new ArrayList<>();
        for(Course course: courseList){
            myCourses.add(Course.toMyCourseDTO(course));
        }

        // 4. CourseListResponseDTO 생성
        CourseListResponseDTO response = new CourseListResponseDTO();
        response.setCourses(myCourses);

        return response;

    }

    // 특정 코스 상세 조회
    public ApiResult getCourseDetail(int userId, int courseId){
        // 1. 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        // 2. 코스 확인
        Course course = courseRepository.findByIdAndUser(courseId, user)
                .orElseThrow(() -> new EntityNotFoundException("코스를 찾을 수 없습니다."));

        CourseDetailResponseDTO response = course.toCourseDetailDTO(course);

        return response;

    }

    public ApiResult deleteCourse(int userId, int courseId){
        // 1. 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        // 2. 코스 확인
        Course course = courseRepository.findByIdAndUser(courseId, user)
                .orElseThrow(() -> new EntityNotFoundException("코스를 찾을 수 없습니다."));

        // 3. 삭제
        courseRepository.delete(course);

        return null;

    }

}