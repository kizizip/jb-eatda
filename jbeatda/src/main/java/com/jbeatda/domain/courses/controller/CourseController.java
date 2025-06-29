package com.jbeatda.domain.courses.controller;

import com.jbeatda.DTO.requestDTO.CreateCourseRequestDTO;
import com.jbeatda.DTO.requestDTO.CourseSelectionRequestDTO;
import com.jbeatda.Mapper.AuthUtils;
import com.jbeatda.domain.courses.service.CourseService;
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
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {


    private final AuthUtils authUtils;
    private final CourseService courseService;


    @Operation(summary = "AI 코스 추천", description = "AI 코스를 추천 받습니다")
    @PostMapping("/recommend")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{ \"courseName\": \"전주 정통 맛집 투어\", \"description\": \"전주에서 국물요리, 밥으로 구성된 맛집 투어를 해 보세요!\", \"storeCount\": 2, \"stores\": [ { \"storeName\": \"할머니 손칼국수\", \"storeImage\": \"https://example.com/images/store1.jpg\", \"area\": \"전주시 완산구\", \"address\": \"전라북도 전주시 완산구 한옥마을길 123\", \"smenu\": \"칼국수, 만두\", \"time\": \"09:00-21:00\", \"holiday\": \"매주 일요일\", \"tel\": \"063-123-4567\", \"parking\": true, \"seats\": \"30석\", \"visitOrder\": 1, \"lat\": 36.096793, \"lng\": 128.419445 }, { \"storeName\": \"전주 비빔밥 원조집\", \"storeImage\": null, \"area\": \"전주시 완산구\", \"address\": \"전라북도 전주시 완산구 태조로 456\", \"smenu\": \"비빔밥, 콩나물국밥\", \"time\": \"10:00-22:00\", \"holiday\": \"매주 월요일\", \"tel\": \"063-234-5678\", \"parking\": false, \"seats\": \"40석\", \"visitOrder\": 2, \"lat\": 36.096793, \"lng\": 128.419445 }, { \"storeName\": \"옛날 막걸리집\", \"storeImage\": \"https://example.com/images/store3.jpg\", \"area\": \"전주시 완산구\", \"address\": \"전라북도 전주시 완산구 교동 789번지\", \"smenu\": \"막걸리, 파전, 도토리묵\", \"time\": \"17:00-01:00\", \"holiday\": \"연중무휴\", \"tel\": \"063-345-6789\", \"parking\": true, \"seats\": \"20석\", \"visitOrder\": 2, \"lat\": 36.096793, \"lng\": 128.419445 } ] }"
                            )
                    )
            )
    })
    public ResponseEntity<?> recommendCourse(
            @AuthenticationPrincipal UserDetails userDetails, // Spring Security에서 현재 인증된 사용자 정보 주입
            @RequestBody CourseSelectionRequestDTO courseSelectionRequestDTO

    ) {
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = courseService.recommendCourse(courseSelectionRequestDTO);

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if (result instanceof ApiResponseDTO<?> errorResult) {
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
            //에러 응답 반환
            return ResponseEntity.status(status).body(errorResult);
        }

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "코스 생성 및 저장", description = " 추천받은 코스를 저장합니다.")
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\" courseId\": 1 }"
                            )
                    )
            )
    })
    public ResponseEntity<?> createCourse(
            @AuthenticationPrincipal UserDetails userDetails, // Spring Security에서 현재 인증된 사용자 정보 주입
            @RequestBody CreateCourseRequestDTO createCourseRequestDTO

    ) {
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = courseService.createCourse(userId, createCourseRequestDTO);

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if (result instanceof ApiResponseDTO<?> errorResult) {
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
            //에러 응답 반환
            return ResponseEntity.status(status).body(errorResult);
        }

        return ResponseEntity.ok(result);
    }



    @Operation(summary = "특정 사용자의 전체 코스 목록 조회 ", description = "특정 사용자의 전체 코스 목록을 조회합니다.")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"courses\": [{\"courseId\": 7, \"courseName\": \"고창 풍천장어 코스\", \"description\": \"고창에서 유명한 풍천장어를 즐길 수 있는 맛집 코스입니다. 풍천장어구이와 다양한 요리를 즐길 수 있습니다.\", \"position\": [\"고창군\"]}, {\"courseId\": 8, \"courseName\": \"고창 풍천장어 코스\", \"description\": \"고창에서 유명한 풍천장어를 즐길 수 있는 맛집 코스입니다. 풍천장어구이와 다양한 요리를 즐길 수 있습니다.\", \"position\": [\"고창군\"]}]}"
                            )
                    )
            )
    })
    public ResponseEntity<?> getCourseList(
            @AuthenticationPrincipal UserDetails userDetails// Spring Security에서 현재 인증된 사용자 정보 주입

    ) {
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = courseService.getCourseList(userId);

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if (result instanceof ApiResponseDTO<?> errorResult) {
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
            //에러 응답 반환
            return ResponseEntity.status(status).body(errorResult);
        }

        return ResponseEntity.ok(result);
    }


    @Operation(summary = "특정 코스 상세 조회 ", description = "특정 코스를 상세조회합니다")
    @GetMapping("/{courseId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"courseId\": 11, \"courseName\": \"전북 맛집 코스\", \"description\": \"전북 지역에서 소고기 요리를 즐길 수 있는 맛집 코스입니다. 차량 이용 및 반일 관광에 적합합니다.\", \"storeCount\": 3, \"stores\": [{\"storeId\": 18, \"sno\": \"1233\", \"storeName\": \"손가네바지락칼국수\", \"address\": \"전북 김제시 동서8길 9\", \"smenu\": \"바지락칼국수^만두(고기/김치)^황태(찜/전골)^안동양반찜닭\", \"visitOrder\": 1, \"lat\": \"35.7991330951813\", \"lng\": \"126.887535592744\"}, {\"storeId\": 19, \"sno\": \"193\", \"storeName\": \"금강장어\", \"address\": \"전북 김제시 청하면 갈산길 36-41\", \"smenu\": \"장어구이^민물새우매운탕\", \"visitOrder\": 2, \"lat\": \"35.908946124835\", \"lng\": \"126.836443577526\"}, {\"storeId\": 20, \"sno\": \"1236\", \"storeName\": \"정자나무가든\", \"address\": \"전북 김제시 만경읍 두내산2길 7\", \"smenu\": \"묵은지 닭도리탕^갈비탕\", \"visitOrder\": 3, \"lat\": \"35.8543333074892\", \"lng\": \"126.818422617076\"}]}"
                            )
                    )
            )
    })
    public ResponseEntity<?> getCourseDetail(
            @AuthenticationPrincipal UserDetails userDetails,// Spring Security에서 현재 인증된 사용자 정보 주입
            @PathVariable int courseId
    ) {
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = courseService.getCourseDetail(userId, courseId);

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if (result instanceof ApiResponseDTO<?> errorResult) {
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
            //에러 응답 반환
            return ResponseEntity.status(status).body(errorResult);
        }

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 코스 삭제 ", description = "특정 코스를 삭제합니다")
    @DeleteMapping("/{courseId}")
    @ApiResponse(
            responseCode = "204",
            description = "코스 삭제 성공"
    )
    public ResponseEntity<?> deleteCourse(
            @AuthenticationPrincipal UserDetails userDetails,// Spring Security에서 현재 인증된 사용자 정보 주입
            @RequestBody List<Integer> courseIds
    ) {
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = courseService.deleteCourses(userId, courseIds);

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if (result instanceof ApiResponseDTO<?> errorResult) {
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
            //에러 응답 반환
            return ResponseEntity.status(status).body(errorResult);
        }

        return ResponseEntity.noContent().build();
    }

}
