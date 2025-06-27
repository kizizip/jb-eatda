package com.jbeatda.domain.menus.controller;

import com.jbeatda.DTO.responseDTO.MenuOnlyResponseDTO;
import com.jbeatda.DTO.responseDTO.MenuResponseDTO;
import com.jbeatda.domain.menus.service.MenuService;
import com.jbeatda.exception.ApiResponseCode;
import com.jbeatda.exception.ApiResponseDTO;
import com.jbeatda.exception.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "지역별 대표 메뉴 조회", description = "특정 지역의 대표 메뉴 리스트를 반환합니다.")
    @GetMapping("/area/{areaId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                            [
                              {
                                "menuId": 24,
                                "menuName": "돌솥밥",
                                "areaId": 12
                              },
                              {
                                "menuId": 25,
                                "menuName": "전주비빔밥",
                                "areaId": 12
                              },
                              {
                                "menuId": 26,
                                "menuName": "콩나물국밥",
                                "areaId": 12
                              }
                            ]
                        """)
                    )
            )
    })
    public ResponseEntity<?> getMenusByAreaId(
            @Parameter(description = "지역 코드 및 이름 목록: 01-고창군, 02-군산시, 03-김제시, 04-남원시, 05-무주군, 06-부안군, 07-순창군, 08-완주군, 09-익산시, 10-임실군, 11-장수군, 12-전주시, 13-정읍시, 14-진안군")
            @PathVariable Integer areaId) {
        ApiResult result = menuService.getMenusByAreaId(areaId);

        if (result instanceof ApiResponseDTO<?> errorResult) {
            HttpStatus status = ApiResponseCode.fromCode(errorResult.getCode()).getHttpStatus();
            return ResponseEntity.status(status).body(errorResult);
        }

        if (result instanceof MenuResponseDTO responseDTO) {
            return ResponseEntity.ok(responseDTO.getMenus());
        }

        return ResponseEntity.ok(result);
    }

    // 메뉴 전체 목록 조회
    @Operation(summary = "전체 메뉴 조회", description = "모든 메뉴의 ID와 이름을 조회합니다.")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                        [
                          {
                            "menuId": 1,
                            "menuName": "비빔밥"
                          },
                          {
                            "menuId": 2,
                            "menuName": "돌솥밥"
                          }
                        ]
                        """)
                    )
            )
    })
    public ResponseEntity<?> getAllMenus() {
        ApiResult result = menuService.getAllMenus();

        if (result instanceof ApiResponseDTO<?> errorResult) {
            HttpStatus status = ApiResponseCode.fromCode(errorResult.getCode()).getHttpStatus();
            return ResponseEntity.status(status).body(errorResult);
        }

        if (result instanceof MenuOnlyResponseDTO responseDTO) {
            return ResponseEntity.ok(responseDTO.getMenus());
        }

        return ResponseEntity.ok(result);
    }


}
