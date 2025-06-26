package com.jbeatda.domain.stores.controller;

import com.jbeatda.DTO.requestDTO.SearchStoreRequestDTO;
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



    @Operation(summary = "특정 식당 상세 정보 조회", description = "특정 지역의 상세 정보를 조회합니다.")
    @GetMapping("/detail/{storeId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{ \"storeName\": \"연기식당\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f4632e.jpg\", \"area\": \"고창군\", \"address\": \"전북 고창군 아산면 선운대로 2727\", \"smenu\": \"장어구이, 장어탕\", \"time\": \"09:00~21:00\", \"holiday\": \"명절전날 휴무\", \"tel\": \"063-561-3815\", \"sno\": \"43\", \"park\": true, \"lat\": \"35.5086018998288\", \"lng\": \"126.599690739955\" }"
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

    @Operation(summary = "특정 지역의 식당 목록 조회", description = "특정 지역의 식당 목록을 조회합니다.")
    @GetMapping("/area/{areaId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{ \"areaInfo\": { \"areaId\": \"03\", \"areaName\": \"김제시\" }, \"stores\": [ { \"storeId\": 444, \"storeName\": \"그린회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f80393.jpg\", \"address\": \"전북 김제시 금산면 모악로 470\", \"smenu\": \"산채비빔밥, 버섯전골, 한정식\", \"time\": \"09:30~19:30\", \"holiday\": \"매장에 문의\", \"tel\": \"063-548-4090\", \"sno\": \"444\", \"park\": null }, { \"storeId\": 1261, \"storeName\": \"예촌면사무소\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f2c12d.jpg\", \"address\": \"김제시 금구면 금구로 43-1\", \"smenu\": \"국수(멸치/비빔,열무), 국수정식, 된장야채비빔밥, 돌솥콩나물밥\", \"time\": \"11:00~21:00(평일 쉬는 시간 14:30~17:00)\", \"holiday\": \"매장에 문의\", \"tel\": \"063-546-5586\", \"sno\": \"1261\", \"park\": null }, { \"storeId\": 465, \"storeName\": \"두꺼비회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/ff065b.jpg\", \"address\": \"전북 김제시 동서8길 21\", \"smenu\": \"한우(특수부위), 전복한우갈비탕, 육회비빔밥\", \"time\": \"11:00~21:00\", \"holiday\": \"매장에 문의\", \"tel\": \"063-547-9991\", \"sno\": \"465\", \"park\": null }, { \"storeId\": 1232, \"storeName\": \"김제회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/20110317-049.JPG\", \"address\": \"전북 김제시 금산면 모악로 460-11\", \"smenu\": \"산채(정식/비빔밥), 더덕구이, 파전\", \"time\": \"08:00~20:00\", \"holiday\": \"매장에 문의\", \"tel\": \"063-548-4097\", \"sno\": \"1232\", \"park\": null }, { \"storeId\": 450, \"storeName\": \"백송회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f2152e.jpg\", \"address\": \"전북 김제시 요촌서길 9-16\", \"smenu\": \"소고기, 육회비빔밥, 아구찜, 갈비찜, 불낙, 우럭탕\", \"time\": \"09:00~21:00\", \"holiday\": \"매장에 문의\", \"tel\": \"063-542-6999\", \"sno\": \"450\", \"park\": null } ], \"pagination\": { \"currentPage\": 1, \"totalPages\": 1, \"totalCount\": 5, \"pageSize\": 20, \"hasNext\": false, \"hasPrev\": false } }"
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




    @Operation(summary = "식당 검색", description = "식당을 검색합니다.")
    @PostMapping("/search/")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{ \"areaInfo\": { \"areaId\": \"03\", \"areaName\": \"김제시\" }, \"stores\": [ { \"storeId\": 444, \"storeName\": \"그린회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f80393.jpg\", \"address\": \"전북 김제시 금산면 모악로 470\", \"smenu\": \"산채비빔밥, 버섯전골, 한정식\", \"time\": \"09:30~19:30\", \"holiday\": \"매장에 문의\", \"tel\": \"063-548-4090\", \"sno\": \"444\", \"park\": null }, { \"storeId\": 1261, \"storeName\": \"예촌면사무소\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f2c12d.jpg\", \"address\": \"김제시 금구면 금구로 43-1\", \"smenu\": \"국수(멸치/비빔,열무), 국수정식, 된장야채비빔밥, 돌솥콩나물밥\", \"time\": \"11:00~21:00(평일 쉬는 시간 14:30~17:00)\", \"holiday\": \"매장에 문의\", \"tel\": \"063-546-5586\", \"sno\": \"1261\", \"park\": null }, { \"storeId\": 465, \"storeName\": \"두꺼비회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/ff065b.jpg\", \"address\": \"전북 김제시 동서8길 21\", \"smenu\": \"한우(특수부위), 전복한우갈비탕, 육회비빔밥\", \"time\": \"11:00~21:00\", \"holiday\": \"매장에 문의\", \"tel\": \"063-547-9991\", \"sno\": \"465\", \"park\": null }, { \"storeId\": 1232, \"storeName\": \"김제회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/20110317-049.JPG\", \"address\": \"전북 김제시 금산면 모악로 460-11\", \"smenu\": \"산채(정식/비빔밥), 더덕구이, 파전\", \"time\": \"08:00~20:00\", \"holiday\": \"매장에 문의\", \"tel\": \"063-548-4097\", \"sno\": \"1232\", \"park\": null }, { \"storeId\": 450, \"storeName\": \"백송회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f2152e.jpg\", \"address\": \"전북 김제시 요촌서길 9-16\", \"smenu\": \"소고기, 육회비빔밥, 아구찜, 갈비찜, 불낙, 우럭탕\", \"time\": \"09:00~21:00\", \"holiday\": \"매장에 문의\", \"tel\": \"063-542-6999\", \"sno\": \"450\", \"park\": null } ], \"pagination\": { \"currentPage\": 1, \"totalPages\": 1, \"totalCount\": 5, \"pageSize\": 20, \"hasNext\": false, \"hasPrev\": false } }"
                            )
                    )
            )
    })
    public ResponseEntity<?> getStoresDetail(
            @AuthenticationPrincipal UserDetails userDetails, // Spring Security에서 현재 인증된 사용자 정보 주입
            @RequestBody SearchStoreRequestDTO searchStoreRequestDTO

    ){
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = storeService.searchStore(searchStoreRequestDTO);

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
