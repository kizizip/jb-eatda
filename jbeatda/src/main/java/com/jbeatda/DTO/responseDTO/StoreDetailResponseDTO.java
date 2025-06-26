package com.jbeatda.DTO.responseDTO;

import com.jbeatda.exception.ApiResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreDetailResponseDTO implements ApiResult {

    private String storeName;    // 매장명
    private String storeImage;   // 매장 이미지 URL
    private String area;         // 지역
    private String address;      // 주소
    private String smenu;        // 메뉴
    private String time;         // 영업시간
    private String holyday;      // 휴무일
    private String tel;          // 전화번호
    private String sno;          // 매장 일련번호
    private boolean park;        // 주차 가능 여부
    private int seat;        // 좌석 수
    private String lat;          // 위도
    private String lng;          // 경도
}