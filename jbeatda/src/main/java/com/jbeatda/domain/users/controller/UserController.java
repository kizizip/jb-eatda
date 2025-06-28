package com.jbeatda.domain.users.controller;

import com.jbeatda.DTO.requestDTO.UserRequestDTO;
import com.jbeatda.DTO.responseDTO.UserResponseDTO;
import com.jbeatda.Mapper.AuthUtils;
import com.jbeatda.domain.users.entity.User;
import com.jbeatda.domain.users.repository.UserRepository;
import com.jbeatda.domain.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthUtils authUtils;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Controller is working!");
    }

    /**
     * 회원가입
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원가입", description = "프로필 사진과 함께 회원가입")
    public ResponseEntity<UserResponseDTO.SignUpResponse> signUp(
            HttpServletRequest httpRequest, // 디버깅용 추가 (변수명 변경)

            @Parameter(description = "이메일")
            @RequestParam("email") String email,

            @Parameter(description = "비밀번호")
            @RequestParam("password") String password,

            @Parameter(description = "사용자명")
            @RequestParam("userName") String userName,

            @Parameter(description = "프로필 이미지")
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        // 파일 디버깅 추가
        System.out.println("=== 파일 디버깅 ===");
        System.out.println("profileImage null? " + (profileImage == null));
        if (profileImage != null) {
            System.out.println("파일명: " + profileImage.getOriginalFilename());
            System.out.println("파일 크기: " + profileImage.getSize());
            System.out.println("파일 타입: " + profileImage.getContentType());
            System.out.println("파일 비어있음? " + profileImage.isEmpty());
        }
        System.out.println("========================");

        try {
            // DTO 생성 (변수명 변경)
            UserRequestDTO.SignUpRequest signUpRequest = UserRequestDTO.SignUpRequest.builder()
                    .email(email)
                    .password(password)
                    .userName(userName)
                    .build();

            // 간단한 validation
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("이메일은 필수입니다.");
            }
            if (password == null || password.length() < 8) {
                throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
            }
            if (userName == null || userName.isBlank()) {
                throw new IllegalArgumentException("사용자명은 필수입니다.");
            }

            UserResponseDTO.SignUpResponse response = userService.signUp(signUpRequest, profileImage);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (RuntimeException e) {
            throw new RuntimeException("회원가입 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO.LoginResponse> login(
            @Valid @RequestBody UserRequestDTO.LoginRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Authorization 헤더 무시하거나 로그만 찍기
        log.info("로그인 요청 Authorization 헤더: {}", authHeader);

        try {
            UserResponseDTO.LoginResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<UserResponseDTO.RefreshTokenResponse> refreshToken(
            @Valid @RequestBody UserRequestDTO.RefreshTokenRequest request) {
        try {
            UserResponseDTO.RefreshTokenResponse response = userService.refreshToken(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<UserResponseDTO.LogoutResponse> logout(
            @Valid @RequestBody UserRequestDTO.LogoutRequest request) {
        try {
            UserResponseDTO.LogoutResponse response = userService.logout(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping
    public ResponseEntity<UserResponseDTO.WithdrawalResponse> withdrawal() {
        try {
            UserResponseDTO.WithdrawalResponse response = userService.withdrawal();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


//    회원 정보 조회
    @GetMapping
    public ResponseEntity<?> getUserInfo(
            @AuthenticationPrincipal UserDetails userDetails

    ) {
        try {
            Integer userId = userDetails != null ?
                    authUtils.getUserIdFromUserDetails(userDetails) :
                    authUtils.getCurrentUserId();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            UserResponseDTO.UserInfoResponse response = UserResponseDTO.UserInfoResponse.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .userName(user.getUserName())
                    .profileImage(user.getProfileImageUrl())
                    .createdAt(user.getCreatedAt())
                    .build();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}