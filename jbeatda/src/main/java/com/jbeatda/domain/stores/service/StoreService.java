package com.jbeatda.domain.stores.service;

import com.jbeatda.DTO.external.JbStoreDetailApiResponseDTO;
import com.jbeatda.DTO.external.JbStoreListApiResponseDTO;
import com.jbeatda.DTO.requestDTO.SearchStoreRequestDTO;
import com.jbeatda.DTO.responseDTO.BookmarkListResponseDTO;
import com.jbeatda.DTO.responseDTO.JbListResponseDTO;
import com.jbeatda.DTO.responseDTO.StoreDetailResponseDTO;
import com.jbeatda.DTO.responseDTO.StoreResponseDTO;
import com.jbeatda.Mapper.StoreDetailMapper;
import com.jbeatda.Mapper.StoreMapper;
import com.jbeatda.domain.stores.client.JbStoreApiClient;
import com.jbeatda.domain.stores.client.KakaoClient;
import com.jbeatda.domain.stores.entity.Bookmark;
import com.jbeatda.domain.stores.entity.Store;
import com.jbeatda.domain.stores.repository.BookmarkRepository;
import com.jbeatda.domain.stores.repository.StoreRepository;
import com.jbeatda.domain.users.entity.User;
import com.jbeatda.domain.users.repository.UserRepository;
import com.jbeatda.exception.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final KakaoClient kakaoClient;
    private final StoreDetailMapper storeDetailMapper;
    private final JbStoreApiClient jbStoreApiClient;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

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
    public ApiResult getStoresDetail(int storeNo) {
        String sno = String.valueOf(storeNo);

        // 1. store 테이블에서 sno로 검색
        Optional<Store> existingStore = storeRepository.findBySno(sno);

        if(existingStore.isPresent()){

            //1-1. store 테이블에 데이터가 있으면 이것을 사용
            Store store = existingStore.get();
            log.info("Store 테이블에서 매장 정보 조회 완료 - sno: {}, name: {}", sno, store.getStoreName());

            StoreDetailResponseDTO response = store.toStoreDetailResponseDTO(store);
            return response;

        } else{
            // 1. JB API 호출
            JbStoreDetailApiResponseDTO.StoreDetail apiItem = jbStoreApiClient.jbStoreDetail(sno);

//            log.info("식당메뉴 {}", apiItem.getSmenu());

            // 2. 경도, 위도 받아오기
            List<String> point = kakaoClient.getPoint(apiItem.getAddress());

            // 3. DTO 변환
            return storeDetailMapper.toJbStoreDetailResponse(apiItem, point);

        }

    }

    // 식당 즐겨찾기(북마크)
    public ApiResult createBookmark(int userId, int storeNo){

        String sno = String.valueOf(storeNo);

        // 1. 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        //  store 테이블에서 sno로 검색
        Optional<Store> existingStore = storeRepository.findBySno(sno);

        Store targetStore;

        if(existingStore.isPresent()){

            //1-1. store 테이블에 데이터가 있으면 이것을 사용
            targetStore = existingStore.get();
            log.info("Store 테이블에서 매장 정보 조회 완료 - sno: {}, name: {}", sno, targetStore.getStoreName());


        } else{
            // 테이블에 데이터가 없으면 API호출로 받아옴
            log.info("Store 정보가 없어서 공공 API 호출 - sno: {}", sno);

            // 2-1.  API 호출
            JbStoreDetailApiResponseDTO.StoreDetail storeDetail = jbStoreApiClient.jbStoreDetail(sno);

            if (storeDetail == null) {
                throw new EntityNotFoundException("매장 정보를 찾을 수 없습니다. SNO: " + sno);
            }

            // 2-2. 좌표 가져오기
            List<String> coordinates = kakaoClient.getPoint(storeDetail.getAddress());

            // 2-3. Store 저장
            Store newStore = Store.fromStoreDetail(storeDetail, coordinates);
            targetStore = storeRepository.save(newStore);
            log.info("새로운 Store 저장 완료 - sno: {}, name: {}", sno, targetStore.getStoreName());

        }

        // 3. 중복 북마크 확인
        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserAndStore(user, targetStore);
        if (existingBookmark.isPresent()) {
            log.warn("중복 북마크 시도 - userId: {}, storeId: {}", userId, targetStore.getId());
            return ApiResponseDTO.fail(ApiResponseCode.BOOKMARK_ALREADY_EXISTS);
        }


        // 4. 북마크 생성 및 저장
        Bookmark bookMark = Bookmark.createEntity(user, targetStore);

        Bookmark savedBookMark = bookmarkRepository.save(bookMark);
        log.info("북마크 생성 완료 - userId: {}, storeId: {}, bookmarkId: {}",
                userId, targetStore.getId(), savedBookMark.getId());

        return null;


    }

    // 북마크 목록 리스트
    public ApiResult getBookmarkList(int userId){
        // 1. 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        // 2. 북마크 리스트
        List<Bookmark> bookmarks = bookmarkRepository.findByUserOrderByCreatedAtDesc(user);

        // DTO 변환
        BookmarkListResponseDTO response = BookmarkListResponseDTO.fromBookmarks(bookmarks);

        log.info("사용자 북마크 조회 완료 - userId: {}, 북마크 개수: {}", userId, response.getBookmarks().size());

        return response;

    }

    // 북마크 삭제
    @Transactional
    public ApiResult deleteBookmark(int userId, List<Integer> storeIds) {
        try {
            // 1. 입력 검증
            if (storeIds == null || storeIds.isEmpty()) {
                return ApiResponseDTO.fail(ApiResponseCode.BAD_REQUEST);
            }

            // 2. 유저 존재 확인
            boolean userExists = userRepository.existsById(userId);
            if (!userExists) {
                return ApiResponseDTO.fail(ApiResponseCode.NOT_FOUND_USER);
            }

            // 3. 해당 유저의 북마크 중 존재하는 매장 ID들만 조회
            List<Integer> validStoreIds = bookmarkRepository.findStoreIdsByUserIdAndStoreIdIn(userId, storeIds);

            if (validStoreIds.isEmpty()) {
                return ApiResponseDTO.fail(ApiResponseCode.BOOKMARK_NOT_FOUND);
            }

            // 4. 존재하지 않는 북마크 체크
            if (validStoreIds.size() != storeIds.size()) {
                List<Integer> notFoundStoreIds = storeIds.stream()
                        .filter(id -> !validStoreIds.contains(id))
                        .collect(Collectors.toList());

                log.warn("존재하지 않는 북마크 매장 ID들: {}", notFoundStoreIds);

                String errorMessage = String.format("다음 매장들의 북마크를 찾을 수 없습니다: %s", notFoundStoreIds);
                return new ApiResponseDTO<>(
                        ApiResponseCode.BOOKMARK_NOT_FOUND.getCode(),
                        errorMessage,
                        null
                );
            }

            // 5. 일괄 삭제 실행
            int deletedCount = bookmarkRepository.deleteByUserIdAndStoreIdIn(userId, validStoreIds);

            if (deletedCount != validStoreIds.size()) {
                log.error("북마크 삭제 예상 개수와 실제 삭제 개수가 다름. 예상: {}, 실제: {}",
                        validStoreIds.size(), deletedCount);
                return ApiResponseDTO.fail(ApiResponseCode.SERVER_ERROR);
            }

            log.info("북마크 삭제 완료 - userId: {}, 삭제된 북마크 수: {}", userId, deletedCount);
            return null; // 성공

        } catch (Exception e) {
            log.error("북마크 삭제 중 오류 발생 - userId: {}, storeIds: {}", userId, storeIds, e);
            return ApiResponseDTO.fail(ApiResponseCode.SERVER_ERROR);
        }
    }

    // 단일 북마크 삭제 (오버로딩)
    @Transactional
    public ApiResult deleteBookmark(int userId, int storeId) {
        return deleteBookmark(userId, List.of(storeId));
    }


}
