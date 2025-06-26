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

}
