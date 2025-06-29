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

import java.util.List;

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
                                    value = "{ \"areaInfo\": { \"areaId\": \"01\", \"areaName\": \"고창군\" }, \"stores\": [ { \"storeId\": 1129, \"storeName\": \"우진갯벌풍천장어\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/20110317-219.JPG\", \"address\": \"전북 고창군 고창읍 상월1길 7\", \"smenu\": \"풍천장어구이\", \"time\": \"11:00~21:00(쉬는 시간 15:00~16:30)\", \"tel\": \"063-564-0101\", \"sno\": \"1129\" }, { \"storeId\": 1123, \"storeName\": \"물레방아\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/20110317-099.JPG\", \"address\": \"전북 고창군 아산면 선운사로 114-1\", \"smenu\": \"풍천장어구이(소금,양념)\", \"time\": \"09:00~21:00\", \"tel\": \"063-562-6315\", \"sno\": \"1123\" }, { \"storeId\": 871, \"storeName\": \"필봉솥뚜껑삼겹살\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/fbb020.jpg\", \"address\": \"전북 고창군 고창읍 월곡8길 6\", \"smenu\": \"생삼겹살, 생가브리살, 차돌박이\", \"time\": \"11:30~22:00(쉬는 시간 14:00~16:30)\", \"tel\": \"063-564-8283\", \"sno\": \"871\" }, { \"storeId\": 866, \"storeName\": \"우정회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f2b504.jpg\", \"address\": \"전북 고창군 심원면 심원로 196\", \"smenu\": \"간장게장\", \"time\": \"10:00~20:00\", \"tel\": \"063-561-2486\", \"sno\": \"866\" }, { \"storeId\": 1124, \"storeName\": \"유달식당\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/20110317-230.JPG\", \"address\": \"전북 고창군 아산면 선운사로 74\", \"smenu\": \"풍천장어, 장어탕\", \"time\": \"09:00~22:00\", \"tel\": \"063-562-2231\", \"sno\": \"1124\" }, { \"storeId\": 43, \"storeName\": \"연기식당\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f4632e.jpg\", \"address\": \"전북 고창군 아산면 선운대로 2727\", \"smenu\": \"보통장어, 장어탕\", \"time\": \"09:00~21:00\", \"tel\": \"063-561-3815\", \"sno\": \"43\" }, { \"storeId\": 868, \"storeName\": \"청원가든\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/fb9621.jpg\", \"address\": \"전북 고창군 아산면 선운사로 67\", \"smenu\": \"장어구이(소금/양념) , 더덕무침, 장어탕\", \"time\": \"10:00~21:00\", \"tel\": \"063-564-0414\", \"sno\": \"868\" }, { \"storeId\": 869, \"storeName\": \"초원풍천장어\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f8bd1b.jpg\", \"address\": \"전북 고창군 아산면 선운사로 100-1\", \"smenu\": \"장어구이(소금/양념), 더덕무침\", \"time\": \"10:30~21:00\", \"tel\": \"063-564-4047\", \"sno\": \"869\" }, { \"storeId\": 1131, \"storeName\": \"흥성회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/20110317-337.JPG\", \"address\": \"전북 고창군 흥덕면 선운대로 3811\", \"smenu\": \"볼태기탕\", \"time\": \"11:00~20:00(쉬는 시간 14시~17시)\", \"tel\": \"063-564-8864\", \"sno\": \"1131\" }, { \"storeId\": 862, \"storeName\": \"바다마을 장어구이\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f17351.jpg\", \"address\": \"전북 고창군 해리면 명사십리로 914\", \"smenu\": \"장어구이(소금/양념)\", \"time\": \"11:00~21:00\", \"tel\": \"063-564-7092\", \"sno\": \"862\" }, { \"storeId\": 867, \"storeName\": \"인천가든\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f2ebd1.jpg\", \"address\": \"전북 고창군 아산면 원평길 9\", \"smenu\": \"새우탕, 메기탕, 송사리탕\", \"time\": \"11:00~17:00(재료소진시 조기 종료)\", \"tel\": \"063-564-8643\", \"sno\": \"867\" }, { \"storeId\": 217, \"storeName\": \"다은회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f850f5.jpg\", \"address\": \"전북 고창군 고창읍 동산7길 1\", \"smenu\": \"백합정식, 백합탕/죽/전/회, 오리약찜, 생선찜/탕\", \"time\": \"11:00~21:00(쉬는 시간 15:00~17:00)\", \"tel\": \"063-564-3304\", \"sno\": \"217\" }, { \"storeId\": 146, \"storeName\": \"강촌숯불장어구이\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/146-m-f707a6.jpg\", \"address\": \"전북 고창군 아산면 인천강서길 6\", \"smenu\": \"풍천장어, 메기매운탕\", \"time\": \"10:00~21:00\", \"tel\": \"063-563-3471\", \"sno\": \"146\" }, { \"storeId\": 46, \"storeName\": \"유신식당\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/fb6087.jpg\", \"address\": \"전북 고창군 아산면 선운사로 25\", \"smenu\": \"풍천장어구이(양념/소금), 장어탕, 더덕무침\", \"time\": \"10:00~20:00\", \"tel\": \"063-562-1566\", \"sno\": \"46\" }, { \"storeId\": 152, \"storeName\": \"산장회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/fb2c92.jpg\", \"address\": \"전북 고창군 아산면 중촌길 20-5\", \"smenu\": \"풍천장어구이, 산채비빔밥\", \"time\": \"07:00~20:00 (아침식사 가능)\", \"tel\": \"063-563-3434\", \"sno\": \"152\" }, { \"storeId\": 1128, \"storeName\": \"강나루풍천장어\", \"storeImage\": null, \"address\": \"전북 고창군 부안면 연기길 17\", \"smenu\": \"풍천장어구이(1인분), 장어탕\", \"time\": \"10:00~20:00(화, 수 영업시간 유동적)\", \"tel\": \"063-561-5592\", \"sno\": \"1128\" }, { \"storeId\": 864, \"storeName\": \"우리회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/fb9fbd.jpg\", \"address\": \"전북 고창군 아산면 선운사로 71\", \"smenu\": \"장어구이, 장어탕\", \"time\": \"10:00~21:00\", \"tel\": \"063-564-4279\", \"sno\": \"864\" }, { \"storeId\": 1125, \"storeName\": \"옛날집\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f7dc3c.jpg\", \"address\": \"전북 고창군 아산면  선운사로 77\", \"smenu\": \"장어구이, 더덕무침\", \"time\": \"09:00~21:00\", \"tel\": \"063-562-9289\", \"sno\": \"1125\" }, { \"storeId\": 180, \"storeName\": \"고향식당\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/fc340f.jpg\", \"address\": \"전북 고창군 아산면 중촌길 20-3\", \"smenu\": \"풍천장어구이, 풍천장어정식, 더덕백반, 돌솥산채비빔밥\", \"time\": \"08:00~21:00\", \"tel\": \"063-563-1326\", \"sno\": \"180\" }, { \"storeId\": 863, \"storeName\": \"아산가든\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f6ce1b.gif\", \"address\": \"전북 고창군 아산면 선운사로 116\", \"smenu\": \"장어구이(소금/양념)\", \"time\": \"11:00~20:30\", \"tel\": \"063-564-3200\", \"sno\": \"863\" }, { \"storeId\": 1130, \"storeName\": \"태흥갈비\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/20110317-294.JPG\", \"address\": \"전북 고창군 고창읍  중앙로 306-4\", \"smenu\": \"한우생갈비,샤브샤브, 돼지갈비, 삽겹살, 육회/육사시미\", \"time\": \"11:30~21:00(쉬는 시간 15시~17시)\", \"tel\": \"063-564-2223\", \"sno\": \"1130\" }, { \"storeId\": 872, \"storeName\": \"할매집풍천장어\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f3f17c.jpg\", \"address\": \"전북 고창군 아산면 선운사로 29\", \"smenu\": \"장어구이(양념,소금)\", \"time\": \"11:30~18:30\", \"tel\": \"063-562-1542\", \"sno\": \"872\" } ], \"pagination\": { \"currentPage\": 1, \"totalPages\": 2, \"totalCount\": 22, \"pageSize\": 20, \"hasNext\": false, \"hasPrev\": false } }"
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
    @PostMapping("/search")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{ \"areaInfo\": { \"areaId\": \"01\", \"areaName\": \"고창군\" }, \"stores\": [ { \"storeId\": 1129, \"storeName\": \"우진갯벌풍천장어\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/20110317-219.JPG\", \"address\": \"전북 고창군 고창읍 상월1길 7\", \"smenu\": \"풍천장어구이\", \"time\": \"11:00~21:00(쉬는 시간 15:00~16:30)\", \"tel\": \"063-564-0101\", \"sno\": \"1129\" }, { \"storeId\": 1123, \"storeName\": \"물레방아\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/20110317-099.JPG\", \"address\": \"전북 고창군 아산면 선운사로 114-1\", \"smenu\": \"풍천장어구이(소금,양념)\", \"time\": \"09:00~21:00\", \"tel\": \"063-562-6315\", \"sno\": \"1123\" }, { \"storeId\": 871, \"storeName\": \"필봉솥뚜껑삼겹살\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/fbb020.jpg\", \"address\": \"전북 고창군 고창읍 월곡8길 6\", \"smenu\": \"생삼겹살, 생가브리살, 차돌박이\", \"time\": \"11:30~22:00(쉬는 시간 14:00~16:30)\", \"tel\": \"063-564-8283\", \"sno\": \"871\" }, { \"storeId\": 866, \"storeName\": \"우정회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f2b504.jpg\", \"address\": \"전북 고창군 심원면 심원로 196\", \"smenu\": \"간장게장\", \"time\": \"10:00~20:00\", \"tel\": \"063-561-2486\", \"sno\": \"866\" }, { \"storeId\": 1124, \"storeName\": \"유달식당\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/20110317-230.JPG\", \"address\": \"전북 고창군 아산면 선운사로 74\", \"smenu\": \"풍천장어, 장어탕\", \"time\": \"09:00~22:00\", \"tel\": \"063-562-2231\", \"sno\": \"1124\" }, { \"storeId\": 43, \"storeName\": \"연기식당\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f4632e.jpg\", \"address\": \"전북 고창군 아산면 선운대로 2727\", \"smenu\": \"보통장어, 장어탕\", \"time\": \"09:00~21:00\", \"tel\": \"063-561-3815\", \"sno\": \"43\" }, { \"storeId\": 868, \"storeName\": \"청원가든\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/fb9621.jpg\", \"address\": \"전북 고창군 아산면 선운사로 67\", \"smenu\": \"장어구이(소금/양념) , 더덕무침, 장어탕\", \"time\": \"10:00~21:00\", \"tel\": \"063-564-0414\", \"sno\": \"868\" }, { \"storeId\": 869, \"storeName\": \"초원풍천장어\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f8bd1b.jpg\", \"address\": \"전북 고창군 아산면 선운사로 100-1\", \"smenu\": \"장어구이(소금/양념), 더덕무침\", \"time\": \"10:30~21:00\", \"tel\": \"063-564-4047\", \"sno\": \"869\" }, { \"storeId\": 1131, \"storeName\": \"흥성회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/20110317-337.JPG\", \"address\": \"전북 고창군 흥덕면 선운대로 3811\", \"smenu\": \"볼태기탕\", \"time\": \"11:00~20:00(쉬는 시간 14시~17시)\", \"tel\": \"063-564-8864\", \"sno\": \"1131\" }, { \"storeId\": 862, \"storeName\": \"바다마을 장어구이\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f17351.jpg\", \"address\": \"전북 고창군 해리면 명사십리로 914\", \"smenu\": \"장어구이(소금/양념)\", \"time\": \"11:00~21:00\", \"tel\": \"063-564-7092\", \"sno\": \"862\" }, { \"storeId\": 867, \"storeName\": \"인천가든\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f2ebd1.jpg\", \"address\": \"전북 고창군 아산면 원평길 9\", \"smenu\": \"새우탕, 메기탕, 송사리탕\", \"time\": \"11:00~17:00(재료소진시 조기 종료)\", \"tel\": \"063-564-8643\", \"sno\": \"867\" }, { \"storeId\": 217, \"storeName\": \"다은회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f850f5.jpg\", \"address\": \"전북 고창군 고창읍 동산7길 1\", \"smenu\": \"백합정식, 백합탕/죽/전/회, 오리약찜, 생선찜/탕\", \"time\": \"11:00~21:00(쉬는 시간 15:00~17:00)\", \"tel\": \"063-564-3304\", \"sno\": \"217\" }, { \"storeId\": 146, \"storeName\": \"강촌숯불장어구이\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/146-m-f707a6.jpg\", \"address\": \"전북 고창군 아산면 인천강서길 6\", \"smenu\": \"풍천장어, 메기매운탕\", \"time\": \"10:00~21:00\", \"tel\": \"063-563-3471\", \"sno\": \"146\" }, { \"storeId\": 46, \"storeName\": \"유신식당\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/fb6087.jpg\", \"address\": \"전북 고창군 아산면 선운사로 25\", \"smenu\": \"풍천장어구이(양념/소금), 장어탕, 더덕무침\", \"time\": \"10:00~20:00\", \"tel\": \"063-562-1566\", \"sno\": \"46\" }, { \"storeId\": 152, \"storeName\": \"산장회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/fb2c92.jpg\", \"address\": \"전북 고창군 아산면 중촌길 20-5\", \"smenu\": \"풍천장어구이, 산채비빔밥\", \"time\": \"07:00~20:00 (아침식사 가능)\", \"tel\": \"063-563-3434\", \"sno\": \"152\" }, { \"storeId\": 1128, \"storeName\": \"강나루풍천장어\", \"storeImage\": null, \"address\": \"전북 고창군 부안면 연기길 17\", \"smenu\": \"풍천장어구이(1인분), 장어탕\", \"time\": \"10:00~20:00(화, 수 영업시간 유동적)\", \"tel\": \"063-561-5592\", \"sno\": \"1128\" }, { \"storeId\": 864, \"storeName\": \"우리회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/fb9fbd.jpg\", \"address\": \"전북 고창군 아산면 선운사로 71\", \"smenu\": \"장어구이, 장어탕\", \"time\": \"10:00~21:00\", \"tel\": \"063-564-4279\", \"sno\": \"864\" }, { \"storeId\": 1125, \"storeName\": \"옛날집\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f7dc3c.jpg\", \"address\": \"전북 고창군 아산면  선운사로 77\", \"smenu\": \"장어구이, 더덕무침\", \"time\": \"09:00~21:00\", \"tel\": \"063-562-9289\", \"sno\": \"1125\" }, { \"storeId\": 180, \"storeName\": \"고향식당\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/fc340f.jpg\", \"address\": \"전북 고창군 아산면 중촌길 20-3\", \"smenu\": \"풍천장어구이, 풍천장어정식, 더덕백반, 돌솥산채비빔밥\", \"time\": \"08:00~21:00\", \"tel\": \"063-563-1326\", \"sno\": \"180\" }, { \"storeId\": 863, \"storeName\": \"아산가든\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f6ce1b.gif\", \"address\": \"전북 고창군 아산면 선운사로 116\", \"smenu\": \"장어구이(소금/양념)\", \"time\": \"11:00~20:30\", \"tel\": \"063-564-3200\", \"sno\": \"863\" }, { \"storeId\": 1130, \"storeName\": \"태흥갈비\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/20110317-294.JPG\", \"address\": \"전북 고창군 고창읍  중앙로 306-4\", \"smenu\": \"한우생갈비,샤브샤브, 돼지갈비, 삽겹살, 육회/육사시미\", \"time\": \"11:30~21:00(쉬는 시간 15시~17시)\", \"tel\": \"063-564-2223\", \"sno\": \"1130\" }, { \"storeId\": 872, \"storeName\": \"할매집풍천장어\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f3f17c.jpg\", \"address\": \"전북 고창군 아산면 선운사로 29\", \"smenu\": \"장어구이(양념,소금)\", \"time\": \"11:30~18:30\", \"tel\": \"063-562-1542\", \"sno\": \"872\" } ], \"pagination\": { \"currentPage\": 1, \"totalPages\": 2, \"totalCount\": 22, \"pageSize\": 20, \"hasNext\": false, \"hasPrev\": false } }"
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





    @Operation(summary = "특정 식당 상세 정보 조회", description = "특정 지역의 상세 정보를 조회합니다.")
    @GetMapping("/detail/{sno}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{ \"storeName\": \"그린회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f80393.jpg\", \"area\": \"김제시\", \"address\": \"전북 김제시 금산면 모악로 470\", \"smenu\": \"한정식, 오리주물럭, 산채비빔밥\", \"time\": \"09:30~19:30\", \"holyday\": \"연중무휴\", \"tel\": \"063-548-4090\", \"sno\": \"444\", \"park\": true, \"seat\": 150, \"lat\": \"35.7182605138428\", \"lng\": \"127.041805724665\" }"
                            )
                    )
            )
    })
    public ResponseEntity<?> getStoresDetail(
            @AuthenticationPrincipal UserDetails userDetails, // Spring Security에서 현재 인증된 사용자 정보 주입
            @PathVariable int sno

    ){
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = storeService.getStoresDetail(sno);

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if (result instanceof ApiResponseDTO<?> errorResult) {
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
            //에러 응답 반환
            return ResponseEntity.status(status).body(errorResult);
        }

        return ResponseEntity.ok(result);
    }



    @Operation(summary = "식당 즐겨찾기(북마크)", description = "해당 식당을 즐겨찾기에 등록합니다")
    @PostMapping("/bookmark/{sno}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "북마크 성공"
            )
    })
    public ResponseEntity<?> createBookmark(
            @AuthenticationPrincipal UserDetails userDetails, // Spring Security에서 현재 인증된 사용자 정보 주입
            @PathVariable int sno

    ){
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = storeService.createBookmark(userId, sno);

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if (result instanceof ApiResponseDTO<?> errorResult) {
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
            //에러 응답 반환
            return ResponseEntity.status(status).body(errorResult);
        }

        return ResponseEntity.status(201).build();
    }



    @Operation(summary = "북마크 목록보기", description = "북마크 목록을 조회합니다.")
    @GetMapping("/bookmark")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{ \"bookmarks\": [ { \"bookmarkId\": 1, \"storeName\": \"그린회관\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f80393.jpg\", \"area\": \"김제시\", \"address\": \"전북 김제시 금산면 모악로 470\", \"smenu\": \"산채비빔밥, 버섯전골, 한정식\", \"sno\": \"444\", \"createdAt\": \"2025-06-27T14:30:00\" }, { \"bookmarkId\": 2, \"storeName\": \"지평선바지락죽\", \"storeImage\": \"http://jbfood.go.kr/datafloder/foodimg/f80125.jpg\", \"area\": \"김제시\", \"address\": \"전북 김제시 중앙로 99\", \"smenu\": \"삼합, 바지락죽, 바지락전, 바지락정식\", \"sno\": \"464\", \"createdAt\": \"2025-06-27T13:15:00\" } ], \"totalCount\": 2 }"
                            )
                    )
            )
    })
    public ResponseEntity<?> getBookmarkList(
            @AuthenticationPrincipal UserDetails userDetails // Spring Security에서 현재 인증된 사용자 정보 주입
    ){
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = storeService.getBookmarkList(userId);

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if (result instanceof ApiResponseDTO<?> errorResult) {
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
            //에러 응답 반환
            return ResponseEntity.status(status).body(errorResult);
        }

        return ResponseEntity.ok(result);
    }


    @Operation(summary = "북마크 삭제", description = "해당 식당 북마크를 삭제합니다.")
    @DeleteMapping("/bookmark/{storeId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "북마크 삭제 성공"
            )
    })
    public ResponseEntity<?> deleteBookmark(
            @AuthenticationPrincipal UserDetails userDetails, // Spring Security에서 현재 인증된 사용자 정보 주입
            @RequestBody List<Integer> storeIds

    ){
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = storeService.deleteBookmark(userId, storeIds);

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if (result instanceof ApiResponseDTO<?> errorResult) {
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
            //에러 응답 반환
            return ResponseEntity.status(status).body(errorResult);
        }

        return ResponseEntity.status(204).build();
    }



}
