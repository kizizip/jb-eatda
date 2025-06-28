package com.jbeatda.domain.stamps.controller;

import com.jbeatda.DTO.requestDTO.StampRequestDTO;
import com.jbeatda.DTO.responseDTO.StampResponseDTO;
import com.jbeatda.Mapper.AuthUtils;
import com.jbeatda.exception.ApiResponseCode;
import com.jbeatda.exception.ApiResponseDTO;
import com.jbeatda.exception.ApiResult;
import com.jbeatda.domain.stamps.service.StampService;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/stamps")
@RequiredArgsConstructor
public class StampController {

    private final AuthUtils authUtils;
    private final StampService stampService;

    @Operation(summary = "사용자의 전체 스탬프 목록 조회", description = "인증된 사용자의 전체 스탬프 목록을 조회 합니다.")
    @ApiResponses(value = @ApiResponse( responseCode = "200", content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = " [ { \"stampId\": 1, \"menuId\": 1, \"image\": \"https://example.com/images/stamp_new1.png\",\"createdAt\": \"2025-06-25T22:00:17.067037\"},{\"stampId\": 2,\"menuId\": 2,\"image\": \"https://example.com/images/stamp_new2.png\",\"createdAt\": \"2025-06-25T22:19:25.455479\"}]")
    )))
    @GetMapping("/user/me")
    public ResponseEntity<?> getAllStamps(@AuthenticationPrincipal UserDetails userDetails) {
        // 현재 인증된 사용자의 ID 가져오기
        Integer userId = resolveUserId(userDetails);
        log.info("userId : {}", userId);
        ApiResult result = stampService.getStampsByUser();
        return buildResponse(result, HttpStatus.OK);
    }

    @Operation(summary = "특정 메뉴에 대한 스탬프 단건 조회", description = "인증된 사용자의 특정 메뉴에 대한 하나의 스탬프를 createdAt 기준으로 1건 조회합니다.")
    @ApiResponses(value = @ApiResponse(responseCode = "200", content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        examples = @ExampleObject(value = " {\"stampId\": 2,\"menuId\": 1,\"image\": \"https://example.com/images/stamp_new2.png\",\"createdAt\": \"2025-06-25T22:19:25.455479\" }")
    )))
    @GetMapping("/user/me/menu/{menuId}")
    public ResponseEntity<?> getStampsByMenu(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer menuId) {
        // 현재 인증된 사용자의 ID 가져오기
        Integer userId = resolveUserId(userDetails);
        log.info("getStampByMenu for userId : {}, menuId: {}", userId, menuId);
        ApiResult result = stampService.getStampsByUserAndMenu(menuId);
        return buildResponse(result, HttpStatus.OK);
    }

    @Operation(summary = "스탬프 등록", description = "user가 사진을 찍으면 stamp에 등록됩니다.")
    @ApiResponses(value = @ApiResponse(responseCode = "201", description = "Created", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(value = "{\"stampId\": 6, \"menuId\": 5, \"image\": \"https://example.com/images/stamp_new.png\", \"createdAt\": \"2025-06-26T03:33:10.745822\"}")
    )))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createStamp(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute StampRequestDTO request,
            @RequestPart(value = "image", required = false) MultipartFile stampImage) {
        Integer userId = resolveUserId(userDetails);
        log.info("createStamp for userId={}", userId);

        ApiResult result = stampService.createStamp(request, stampImage);

        return buildResponse(result, HttpStatus.CREATED);
    }

    private Integer resolveUserId(UserDetails userDetails) {
        return userDetails != null
                ? authUtils.getUserIdFromUserDetails(userDetails)
                : authUtils.getCurrentUserId();
    }

    private ResponseEntity<?> buildResponse(ApiResult result, HttpStatus successStatus) {
        // 응답 결과가 에러인 경우 처리 (ApiResponseDTO 타입으로 캐스팅 가능한 경우)
        if(result instanceof ApiResponseDTO<?> error){
            HttpStatus status = ApiResponseCode.fromCode(error.getCode()).getHttpStatus();
            // 에러 응답 변환
            return ResponseEntity.status(status).body(error);
        }
        return ResponseEntity.status(successStatus)
                .body(result instanceof StampResponseDTO dto ? dto.getStamps() : result);
    }

    // 스탬프 삭제
    @Operation(summary = "스탬프 삭제", description = "인증된 사용자가 자신의 스탬프를 삭제합니다.")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(value = "{\"code\": \"200\", \"message\": \"정상 처리되었습니다.\"}"))))
    @DeleteMapping("/{stampId}")
    public ResponseEntity<?> deleteStamp(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable Integer stampId) {
        Integer userId = resolveUserId(userDetails);
        log.info("deleteStamp for userId: {}, stampId: {}", userId, stampId);

        // userId 파라미터를 서비스에 넘기지 않고, 서비스 내부에서 authUtils.getCurrentUserId() 호출하도록 변경 권장
        ApiResult result = stampService.deleteStamp(stampId);
        return buildResponse(result, HttpStatus.OK);
    }


}
