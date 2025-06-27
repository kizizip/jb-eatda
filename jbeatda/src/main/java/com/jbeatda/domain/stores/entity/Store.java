package com.jbeatda.domain.stores.entity;

import com.jbeatda.DTO.external.JbStoreDetailApiResponseDTO;
import com.jbeatda.DTO.requestDTO.CreateCourseRequestDTO;
import com.jbeatda.DTO.responseDTO.StoreDetailResponseDTO;
import com.jbeatda.domain.courses.entity.CourseStore;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Integer id;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_image")
    private String storeImage;

    @Column(name = "area")
    private String area;

    @Column(name = "address")
    private String address;

    @Column(name = "smenu")
    private String smenu;

    @Column(name = "time")
    private String time;

    @Column(name = "holiday")
    private String holiday;

    @Column(name = "sno")
    private String sno;

    @Column(name = "tel")
    private String tel;

    @Column(name = "park")
    private Boolean park;

    @Column(name = "seat")
    private int seat;

    @Column(name = "lat")
    private String lat; // 위도

    @Column(name = "lng")
    private String lng; // 경도

    // 연관관계
    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseStore> courseStores = new ArrayList<>();



    public static Store fromStoreDetail(JbStoreDetailApiResponseDTO.StoreDetail storeDetail, List<String> coordinates) {
//        // 좌표 추출 (클라이언트 제공 좌표 우선 사용)
//        String latitude = (coordinates != null && coordinates.size() >= 2) ? coordinates.get(0) : storeDetail.getFLatitude();
//        String longitude = (coordinates != null && coordinates.size() >= 2) ? coordinates.get(1) : storeDetail.getFLongitude();

        // 주차 정보 변환 (Y/N → boolean)
        Boolean parkingAvailable = convertParkFlag(storeDetail.getPark());

        // 메뉴 정보 처리 - ^ 를 , 로 변환
//        log.info("메뉴 정보: {}", storeDetail.getSmenu());
        String processedSmenu = storeDetail.getSmenu() != null
                ? storeDetail.getSmenu().replace("^", ", ")
                : null;

        return Store.builder()
                .sno(storeDetail.getSno())
                .storeName(storeDetail.getName())         // API 데이터 사용
                .storeImage(buildImageUrl(storeDetail.getImg())) // API 데이터 사용
                .area(storeDetail.getArea())                   // API 데이터 사용
                .address(storeDetail.getAddress())             // API 데이터 사용
                .smenu(processedSmenu)                 // API 데이터 사용
                .time(storeDetail.getTime())                   // API 데이터 사용
                .holiday(storeDetail.getHolyday())             // API 데이터 사용
                .tel(storeDetail.getTel())                     // API 데이터 사용
                .park(parkingAvailable)                        // API 데이터 사용
                .seat(Integer.parseInt(storeDetail.getSeat())) // API 데이터 사용
                .lat(coordinates.get(0))                                 // 클라이언트 좌표 우선
                .lng(coordinates.get(1))                           // 클라이언트 좌표 우선
                .build();
    }

    /**
     * 이미지 URL 생성 헬퍼 메서드
     */
    private static String buildImageUrl(String imageName) {
        if (imageName == null || imageName.trim().isEmpty() || "null".equals(imageName)) {
            return null;
        }
        // JB API 이미지 베이스 URL과 조합 (파이프 구분자로 분리된 첫 번째 값 사용)
        String fileName = imageName.split("\\|")[0];
        return "http://jbfood.go.kr/datafloder/foodimg/" + fileName;
    }

    /**
     * 주차 플래그 변환 헬퍼 메서드
     */
    private static Boolean convertParkFlag(String parkFlag) {
        if (parkFlag == null || parkFlag.trim().isEmpty()) {
            return null;
        }
        String normalized = parkFlag.trim().toLowerCase();
        return "y".equals(normalized) || "yes".equals(normalized);
    }

    public static StoreDetailResponseDTO toStoreDetailResponseDTO (Store store){

        return StoreDetailResponseDTO.builder()
                .storeName(store.getStoreName())
                .storeImage(store.getStoreImage())
                .area(store.getArea())
                .address(store.getAddress())
                .smenu(store.getSmenu())
                .time(store.getTime())
                .holyday(store.getHoliday())
                .tel(store.getTel())
                .sno(store.getSno())
                .park(store.getPark())
                .seat(store.getSeat()) // Store 엔티티에서 seat 정보 파싱
                .lat(store.getLat())
                .lng(store.getLng())
                .build();
    }



}



