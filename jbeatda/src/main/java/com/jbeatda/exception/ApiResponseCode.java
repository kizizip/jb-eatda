package com.jbeatda.exception;

import org.springframework.http.HttpStatus;

import java.util.Arrays;


public enum ApiResponseCode {

    // 성공
    SUCCESS("200", "요청이 성공했습니다.", HttpStatus.OK),

    // 실패
    BAD_REQUEST("400", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_USER("404-1", "유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NO_STORES_FOUND("404-2", "저장된 가게가 없습니다.", HttpStatus.NOT_FOUND),
    NO_AREAS_FOUND("404-3", "저장된 지역이 없습니다", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("403-1", "권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    SERVER_ERROR("500", "서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ApiResponseCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public HttpStatus getHttpStatus() { return httpStatus; }


    public static ApiResponseCode fromCode(String code) {
        return Arrays.stream(ApiResponseCode.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(ApiResponseCode.SERVER_ERROR); // 기본 fallback
    }

}
