package com.jbeatda.domain.stores.service;

import com.jbeatda.DTO.external.JbStoreDetailApiResponseDTO;
import com.jbeatda.DTO.external.JbStoreListApiResponseDTO;
import com.jbeatda.DTO.requestDTO.SearchStoreRequestDTO;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final KakaoClient kakaoClient;
    private final StoreDetailMapper storeDetailMapper;
    private final JbStoreApiClient jbStoreApiClient;
    private final BookmarkRepository bookMarkRepository;
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
    public ApiResult getStoresDetail(int storeId) {
        String sno = String.valueOf(storeId);

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
    public ApiResult createBookmark(int userId, int storeId){

        String sno = String.valueOf(storeId);

        // 1. 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        // 1. store 테이블에서 sno로 검색
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
        Optional<Bookmark> existingBookmark = bookMarkRepository.findByUserAndStore(user, targetStore);
        if (existingBookmark.isPresent()) {
            throw new IllegalStateException("이미 북마크된 매장입니다.");
        }


        // 4. 북마크 생성 및 저장
        Bookmark bookMark = Bookmark.createEntity(user, targetStore);

        Bookmark savedBookMark = bookMarkRepository.save(bookMark);
        log.info("북마크 생성 완료 - userId: {}, storeId: {}, bookmarkId: {}",
                userId, targetStore.getId(), savedBookMark.getId());

        return null;


    }




}
