package com.jbeatda.DTO.responseDTO;

import com.jbeatda.exception.ApiResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoStoreDetailResponseDTO implements ApiResult {

    private String storeName;
    private String storeImage;
    private String area;
    private String address;
    private String smenu;
    private String time;
    private String holiday;
    private String tel;
    private String sno;
    private boolean park;
    private String lat; // 위도
    private String lng; // 경도




}
