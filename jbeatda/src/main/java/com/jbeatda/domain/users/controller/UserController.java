package com.jbeatda.domain.users.controller;

import com.jbeatda.DTO.requestDTO.UserRequestDTO;
import com.jbeatda.DTO.responseDTO.UserResponseDTO;
import com.jbeatda.domain.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Controller is working!");
    }

    /**
     * 회원가입
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO.SignUpResponse> signUp(
            @Valid @RequestBody UserRequestDTO.SignUpRequest request) {
        try {
            UserResponseDTO.SignUpResponse response = userService.signUp(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
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

}