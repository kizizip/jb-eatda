package com.jbeatda.domain.stores.entity;

import com.jbeatda.DTO.external.JbStoreDetailApiResponseDTO;
import com.jbeatda.DTO.requestDTO.CreateCourseRequestDTO;
import com.jbeatda.domain.courses.entity.CourseStore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "lat")
    private String lat; // 위도

    @Column(name = "lng")
    private String lng; // 경도

    // 연관관계
    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseStore> courseStores = new ArrayList<>();

//    public static Store fromBase(CreateCourseRequestDTO.StoreDTO storeDTO) {
//        return Store.builder()
//                .storeName(storeDTO.getStoreName())
//                .storeImage(storeDTO.getStoreImage())
//                .area(storeDTO.getArea())
//                .address(storeDTO.getAddress())
//                .smenu(storeDTO.getSmenu())
//                .time(storeDTO.getTime())
//                .holiday(storeDTO.getHoliday())
//                .sno(storeDTO.getSno())
//                .tel(storeDTO.getTel())
//                .park(storeDTO.getParking())
//                .lat(storeDTO.getLat())
//                .lng(storeDTO.getLng())
//                .build();
//    }


    public static Store fromStoreDetail(JbStoreDetailApiResponseDTO.StoreDetail storeDetail, List<String> coordinates) {
        // 좌표 추출 (클라이언트 제공 좌표 우선 사용)
        String latitude = (coordinates != null && coordinates.size() >= 2) ? coordinates.get(0) : storeDetail.getFLatitude();
        String longitude = (coordinates != null && coordinates.size() >= 2) ? coordinates.get(1) : storeDetail.getFLongitude();

        // 이미지 URL 생성
        String imageUrl = buildImageUrl(storeDetail.getImg());

        // 주차 정보 변환 (Y/N → boolean)
        Boolean parkingAvailable = convertParkFlag(storeDetail.getPark());

        // 메뉴 정보 정리 (FOOD 우선, 없으면 SMENU 사용)
        String menu = parseMenu(storeDetail.getFood(), storeDetail.getSmenu());

        return Store.builder()
                .sno(storeDetail.getSno())
                .storeName(storeDetail.getName())
                .storeImage(imageUrl)
                .area(storeDetail.getArea())
                .address(storeDetail.getAddress())
                .smenu(menu)
                .time(storeDetail.getTime())
                .holiday(storeDetail.getHolyday())
                .tel(storeDetail.getTel())
                .park(parkingAvailable)
                .lat(latitude)
                .lng(longitude)
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

    /**
     * 메뉴 정보 파싱 헬퍼 메서드
     */
    private static String parseMenu(String food, String smenu) {
        // FOOD 필드를 우선 사용, 없으면 SMENU 사용
        String menu = null;
        if (food != null && !food.trim().isEmpty()) {
            menu = food;
        } else if (smenu != null && !smenu.trim().isEmpty()) {
            menu = smenu;
        }

        if (menu != null) {
            // ^나 | 등의 구분자를 콤마로 변경
            menu = menu.replace("^", ", ").replace("|", ", ");
        }

        return menu;
    }
}



