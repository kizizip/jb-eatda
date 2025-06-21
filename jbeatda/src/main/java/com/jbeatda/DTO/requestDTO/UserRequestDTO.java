package com.jbeatda.DTO.requestDTO;

import lombok.*;

public class UserRequestDTO {

    // 회원가입 Request
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpRequest {
        private String email;
        private String password;
        private String userName;
    }

    // 로그인 Request
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    // 토큰 갱신 Request
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenRequest {
        private String refreshToken;
    }
}
