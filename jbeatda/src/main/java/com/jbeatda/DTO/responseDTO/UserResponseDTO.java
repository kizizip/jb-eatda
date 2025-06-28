package com.jbeatda.DTO.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class UserResponseDTO {

    // 회원가입 Response
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpResponse {
        private Integer userId;
        private String email;
        private String userName;
        private String profileImage;
        private LocalDateTime createdAt;
    }

    // 로그인 Response
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private Integer userId;
        private String email;
        private String userName;
        private String token;
    }

    // 토큰 갱신 Response
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenResponse {
        private String accessToken;
        private String refreshToken;
    }

    // 로그아웃 Response
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogoutResponse {
        private String message;
        private LocalDateTime logoutTime;
    }

    // 회원 탈퇴 Response
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithdrawalResponse {
        private String message;
        private String email;
        private LocalDateTime withdrawalTime;
    }

    // 회원 정보 조회 API
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoResponse {
        private Integer userId;
        private String email;
        private String userName;
        private String profileImage;
        private LocalDateTime createdAt;
    }
}
