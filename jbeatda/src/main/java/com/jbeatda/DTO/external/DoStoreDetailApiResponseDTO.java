package com.jbeatda.DTO.external;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 도지정향토음식점 정보 - 상세정보 조회 응답 결과
 */
public class DoStoreDetailApiResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreDetail {
        private String sno;           // SNO - 일련번호
        private String name;          // NAME - 매장명
        private String area;          // AREA - 지역
        private String address;       // ADDRESS - 주소
        private String tel;           // TEL - 전화번호
        private String time;          // TIME - 영업시간
        private String img;           // IMG - 이미지 URL
        private String seat;          // SEAT - 좌석수
        private String holyday;       // HOLYDAY - 휴무일
        private String park;          // PARK - 주차가능여부 (Y/N)
        private String car;           // CAR - 발렛파킹 여부 (Y/N)
        private String etc;           // ETC - 기타정보
        private String content;       // CONTENT - 매장 상세설명
        private String map;           // MAP - 지도 이미지 URL
        private String seq;           // SEQ - 추가 서비스 정보
        private String cktype;        // CKTYPE - 체크타입 (Y/N)
        private String food;          // FOOD - 대표메뉴
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse {
        private Header header;
        private Body body;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        private String resultCode;    // 결과코드
        private String resultMsg;     // 결과메시지
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body {
        private StoreDetail item;     // 매장 상세정보
    }

}
