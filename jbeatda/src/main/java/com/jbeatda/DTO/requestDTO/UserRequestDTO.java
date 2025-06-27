package com.jbeatda.DTO.requestDTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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

    // 로그아웃 Request
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogoutRequest {
        private String refreshToken; // 선택적으로 리프레시 토큰도 받을 수 있음
    }
}
