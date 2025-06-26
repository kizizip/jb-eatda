package com.jbeatda.exception;

public class AiException extends CustomException {

  public AiException(ApiResponseCode responseCode) {
    super(responseCode, HttpStatusCode.SERVICE_UNAVAILABLE, responseCode.getMessage());
  }

  public AiException(String message) {
    super(ApiResponseCode.AI_RESPONSE_PARSE_ERROR, HttpStatusCode.SERVICE_UNAVAILABLE, message);
  }

  // AI 응답 파싱 전용 생성자
  public static AiException parseError(String originalResponse) {
    return new AiException("AI 응답 파싱에 실패했습니다: " +
            (originalResponse.length() > 100 ?
                    originalResponse.substring(0, 100) + "..." :
                    originalResponse));
  }
}