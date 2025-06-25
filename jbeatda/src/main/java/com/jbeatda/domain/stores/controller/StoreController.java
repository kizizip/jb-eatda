package com.jbeatda.domain.stores.controller;

import com.jbeatda.DTO.requestDTO.PageRequestDTO;
import com.jbeatda.DTO.responseDTO.StoreListResponseDTO;
import com.jbeatda.DTO.responseDTO.StoreResponseDTO;
import com.jbeatda.Mapper.AuthUtils;
import com.jbeatda.domain.stores.service.StoreService;
import com.jbeatda.exception.ApiResponseCode;
import com.jbeatda.exception.ApiResponseDTO;
import com.jbeatda.exception.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final AuthUtils authUtils;
    private final StoreService storeService;



    @Operation(summary = "특정 지역의 식당 목록 조회", description = "특정 지역의 식당 목록을 조회합니다.")
    @GetMapping("/area/{areaId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "[ { \"storeId\": 1, \"storeName\": \"할머니 손칼국수\", \"storeImage\": \"https://example.com/images/store1.jpg\", \"area\": \"전주시 완산구\", \"address\": \"전라북도 전주시 완산구 한옥마을길 123\", \"smenu\": \"칼국수, 만두\", \"time\": \"09:00-21:00\", \"holiday\": \"매주 일요일\", \"tel\": \"063-123-4567\", \"sno\": \"STORE001\", \"park\": true }, { \"storeId\": 2, \"storeName\": \"전주 비빔밥 원조집\", \"storeImage\": null, \"area\": \"전주시 완산구\", \"address\": \"전라북도 전주시 완산구 태조로 456\", \"smenu\": \"비빔밥, 콩나물국밥\", \"time\": \"10:00-22:00\", \"holiday\": \"매주 월요일\", \"tel\": \"063-234-5678\", \"sno\": \"STORE002\", \"park\": false }, { \"storeId\": 3, \"storeName\": \"옛날 막걸리집\", \"storeImage\": \"https://example.com/images/store3.jpg\", \"area\": \"전주시 완산구\", \"address\": \"전라북도 전주시 완산구 교동 789번지\", \"smenu\": \"막걸리, 파전, 도토리묵\", \"time\": \"17:00-01:00\", \"holiday\": \"연중무휴\", \"tel\": \"063-345-6789\", \"sno\": \"STORE003\", \"park\": true }]"
                            )
                    )
            )
    })
    public ResponseEntity<?> getStoresByArea(
            @AuthenticationPrincipal UserDetails userDetails, // Spring Security에서 현재 인증된 사용자 정보 주입
            @PathVariable String areaId

    ) {
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = storeService.getStoresByArea(areaId);

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if (result instanceof ApiResponseDTO<?> errorResult) {
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
            //에러 응답 반환
            return ResponseEntity.status(status).body(errorResult);
        }

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 식당 상세 정보 조회", description = "특정 지역의 상세 정보를 조회합니다.")
    @GetMapping("/detail/{storeId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "[ { \"storeId\": 1, \"storeName\": \"할머니 손칼국수\", \"storeImage\": \"https://example.com/images/store1.jpg\", \"area\": \"전주시 완산구\", \"address\": \"전라북도 전주시 완산구 한옥마을길 123\", \"smenu\": \"칼국수, 만두\", \"time\": \"09:00-21:00\", \"holiday\": \"매주 일요일\", \"tel\": \"063-123-4567\", \"sno\": \"STORE001\", \"park\": true }, { \"storeId\": 2, \"storeName\": \"전주 비빔밥 원조집\", \"storeImage\": null, \"area\": \"전주시 완산구\", \"address\": \"전라북도 전주시 완산구 태조로 456\", \"smenu\": \"비빔밥, 콩나물국밥\", \"time\": \"10:00-22:00\", \"holiday\": \"매주 월요일\", \"tel\": \"063-234-5678\", \"sno\": \"STORE002\", \"park\": false }, { \"storeId\": 3, \"storeName\": \"옛날 막걸리집\", \"storeImage\": \"https://example.com/images/store3.jpg\", \"area\": \"전주시 완산구\", \"address\": \"전라북도 전주시 완산구 교동 789번지\", \"smenu\": \"막걸리, 파전, 도토리묵\", \"time\": \"17:00-01:00\", \"holiday\": \"연중무휴\", \"tel\": \"063-345-6789\", \"sno\": \"STORE003\", \"park\": true }]"
                            )
                    )
            )
    })
    public ResponseEntity<?> getStoresDetail(
            @AuthenticationPrincipal UserDetails userDetails, // Spring Security에서 현재 인증된 사용자 정보 주입
            @PathVariable int storeId

    ){
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = storeService.getStoresDetail(storeId);

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if (result instanceof ApiResponseDTO<?> errorResult) {
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
            //에러 응답 반환
            return ResponseEntity.status(status).body(errorResult);
        }

        return ResponseEntity.ok(result);
    }



//
//    @Operation(summary = "전체 가게 목록 조회", description = "전체 가게 목록을 조회합니다. UI에는 쓰이지 않을 듯한 API입니다.")
//    @GetMapping
//    @ApiResponses(value = {
//            @ApiResponse(
//                    responseCode = "200",
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON_VALUE,
//                            examples = @ExampleObject(
//                                    value = "[ { \"storeId\": 1, \"storeName\": \"할머니 손칼국수\", \"storeImage\": \"https://example.com/images/store1.jpg\", \"area\": \"전주시 완산구\", \"address\": \"전라북도 전주시 완산구 한옥마을길 123\", \"smenu\": \"칼국수, 만두\", \"time\": \"09:00-21:00\", \"holiday\": \"매주 일요일\", \"tel\": \"063-123-4567\", \"sno\": \"STORE001\", \"park\": true }, { \"storeId\": 2, \"storeName\": \"전주 비빔밥 원조집\", \"storeImage\": null, \"area\": \"전주시 완산구\", \"address\": \"전라북도 전주시 완산구 태조로 456\", \"smenu\": \"비빔밥, 콩나물국밥\", \"time\": \"10:00-22:00\", \"holiday\": \"매주 월요일\", \"tel\": \"063-234-5678\", \"sno\": \"STORE002\", \"park\": false }, { \"storeId\": 3, \"storeName\": \"옛날 막걸리집\", \"storeImage\": \"https://example.com/images/store3.jpg\", \"area\": \"전주시 완산구\", \"address\": \"전라북도 전주시 완산구 교동 789번지\", \"smenu\": \"막걸리, 파전, 도토리묵\", \"time\": \"17:00-01:00\", \"holiday\": \"연중무휴\", \"tel\": \"063-345-6789\", \"sno\": \"STORE003\", \"park\": true }]"
//                            )
//                    )
//            )
//    })
//    public ResponseEntity<?> getAllStores(
//            @AuthenticationPrincipal UserDetails userDetails // Spring Security에서 현재 인증된 사용자 정보 주입
//    ) {
//        // 현재 인증된 사용자의 ID 가져오기
//        // 1. 인증된 사용자가 있으면 userDetails에서 ID추출
//        // 2. userDetails가 null이면 다른 방식으로 현재 사용자 ID 가져오기 시도
//        // userId 필요하면 그냥 이거 복붙해서 쓰시면 됩니다. 필요없으면 말고... 이건 필요없는 api인데 예시로 적음
//        Integer userId = userDetails != null ?
//                authUtils.getUserIdFromUserDetails(userDetails) :
//                authUtils.getCurrentUserId();
//
//        log.info("userId: {}", userId);
//
//        ApiResult result = storeService.getAllStores();
//
//        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
//        if (result instanceof ApiResponseDTO<?> errorResult) {
//            String code = errorResult.getCode(); //에러 코드 추출
//            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
//            //에러 응답 반환
//            return ResponseEntity.status(status).body(errorResult);
//        }
//
//        // 응답 결과가 StoreResponseDTO인 경우 (정상 응답)
//        if (result instanceof StoreResponseDTO responseDTO) {
//            return ResponseEntity.ok(responseDTO.getStores()); // 가게 목록만 추출해서 반환
//        }
//
//        return ResponseEntity.ok(result);
//    }

}
