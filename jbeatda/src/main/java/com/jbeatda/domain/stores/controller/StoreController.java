package com.jbeatda.domain.stores.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final AuthUtils authUtils;
    private final StoreService storeService;

    @Operation(summary = "전체 가게 목록 조회", description = "전체 가게 목록을 조회합니다. UI에는 쓰이지 않을 듯한 API입니다.")
    @GetMapping
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
    public ResponseEntity<?> getAllStores(
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        Integer userId = userDetails != null ?
                authUtils.getUserIdFromUserDetails(userDetails) :
                authUtils.getCurrentUserId();

        log.info("userId: {}", userId);

        ApiResult result = storeService.getAllStores();

        if (result instanceof ApiResponseDTO<?> errorResult) {
            String code = errorResult.getCode();
            HttpStatus status = ApiResponseCode.fromCode(code).getHttpStatus();
            ResponseEntity.status(status).body(errorResult);
        }

        if (result instanceof StoreResponseDTO responseDTO) {
            return ResponseEntity.ok(responseDTO.getStores());
        }

        return ResponseEntity.ok(result);
    }
}
