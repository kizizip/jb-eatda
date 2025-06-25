package com.jbeatda.exception;

public class ExternalApiException extends CustomException {

    public ExternalApiException(ApiResponseCode responseCode) {
        super(responseCode, HttpStatusCode.SERVICE_UNAVAILABLE, responseCode.getMessage());
    }

    public ExternalApiException(String message) {
        super(ApiResponseCode.EXTERNAL_API_ERROR, HttpStatusCode.SERVICE_UNAVAILABLE, message);
    }
}