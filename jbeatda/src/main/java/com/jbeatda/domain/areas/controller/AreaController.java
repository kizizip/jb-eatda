package com.jbeatda.domain.areas.controller;

import com.jbeatda.DTO.responseDTO.AreaResponseDTO;
import com.jbeatda.DTO.responseDTO.StoreResponseDTO;
import com.jbeatda.Mapper.AuthUtils;
import com.jbeatda.domain.areas.service.AreaService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AuthUtils authUtils;
    private final AreaService areaService;

    @Operation(summary = "지역 목록 전체 조회", description = "전체 지역 목록을 조회합니다. 스탬프에서 사용됩니다.")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              { "areaId": 1, "areaName": "고창군" },
                                              { "areaId": 2, "areaName": "군산시" },
                                              { "areaId": 3, "areaName": "김제시" },
                                              { "areaId": 4, "areaName": "남원시" },
                                              { "areaId": 5, "areaName": "무주군" },
                                              { "areaId": 6, "areaName": "부안군" },
                                              { "areaId": 7, "areaName": "순창군" },
                                              { "areaId": 8, "areaName": "완주군" },
                                              { "areaId": 9, "areaName": "익산시" },
                                              { "areaId": 10, "areaName": "임실군" },
                                              { "areaId": 11, "areaName": "장수군" },
                                              { "areaId": 12, "areaName": "전주시" },
                                              { "areaId": 13, "areaName": "정읍시" },
                                              { "areaId": 14, "areaName": "진안군" }
                                            ]
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<?> getAllAreas(
            @AuthenticationPrincipal UserDetails userDetails
            ){
        // 현재 인증된 사용자의 ID 가져오기
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId : {}", userId);

        ApiResult result = areaService.getAllAreas();

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if(result instanceof ApiResponseDTO<?> errorResult){
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus();
            // 에러 응답 변환
            return ResponseEntity.status(status).body(errorResult);
        }

        // 응답 결과가 StoreResponseDTO인 경우 (정상 응답)
        if(result instanceof AreaResponseDTO responseDTO){
            return ResponseEntity.ok(responseDTO.getAreas()); // 지역 목록만 추출해서 반환
        }

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "지역 목록 단건 조회", description = "한 건의 지역을 조회합니다.")
    @GetMapping("/{areaId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value =  """
                                    {
                                      "areaId": 12,
                                      "areaName": "전주시"
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<?> getAreaById(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer areaId){
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}, areaId: {}", userId, areaId);

        ApiResult result = areaService.getAreaById(areaId);

        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if(result instanceof ApiResponseDTO<?> errorResult){
            String code = errorResult.getCode(); //에러 코드 추출
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus(); //코드에 맞는 http 상태 가져오기
            //에러 응답 반환
            return ResponseEntity.status(status).body(errorResult);
        }

        // 응답 결과가 AreaResponseDTO인 경우 (정상 응답)
        if (result instanceof AreaResponseDTO.AreaDTO areaDTO) {
            return ResponseEntity.ok(areaDTO); // 가게 목록만 추출해서 반환
        }

        return ResponseEntity.ok(result);
    }
}


