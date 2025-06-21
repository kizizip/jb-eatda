package com.jbeatda.domain.users.service;

import com.jbeatda.DTO.requestDTO.UserRequestDTO;
import com.jbeatda.DTO.responseDTO.UserResponseDTO;
import com.jbeatda.config.JwtProvider;
import com.jbeatda.domain.users.entity.User;
import com.jbeatda.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입
    @Transactional
    public UserResponseDTO.SignUpResponse signUp(UserRequestDTO.SignUpRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userName(request.getUserName())
                .build();

        User savedUser = userRepository.save(user);

        return UserResponseDTO.SignUpResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .userName(savedUser.getUserName())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    // 로그인
    public UserResponseDTO.LoginResponse login(UserRequestDTO.LoginRequest request) {
        try {


            log.info("=== 로그인 시도 ===");
            log.info("요청 이메일: {}", request.getEmail());
            log.info("요청 비밀번호 길이: {}", request.getPassword() != null ? request.getPassword().length() : "null");


            // 사용자 조회
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

            log.info("DB에서 찾은 사용자: {}", user.getEmail());
            log.info("DB 저장된 비밀번호 (앞 10자): {}", user.getPassword().substring(0, Math.min(10, user.getPassword().length())));
            log.info("입력받은 비밀번호: {}", request.getPassword());

            // 비밀번호 검증
            boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
            log.info("비밀번호 매칭 결과: {}", passwordMatch);


            // 비밀번호 검증
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
            }

            log.info("로그인 성공!");

            // 토큰 생성
            log.info("토큰 생성 시작 - userId: {}, email: {}", user.getId(), user.getEmail());

            String accessToken = null;
            String refreshToken = null;

            try {
                accessToken = jwtProvider.generateToken(user.getEmail(), user.getId());
                log.info("액세스 토큰 생성 성공");
            } catch (Exception e) {
                log.error("액세스 토큰 생성 실패: {}", e.getMessage(), e);
                throw e;
            }

            try {
                refreshToken = jwtProvider.generateRefreshToken(user.getEmail(), user.getId());
                log.info("리프레시 토큰 생성 성공");
            } catch (Exception e) {
                log.error("리프레시 토큰 생성 실패: {}", e.getMessage(), e);
                throw e;
            }

            return UserResponseDTO.LoginResponse.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .userName(user.getUserName())
                    .token(accessToken)
                    .build();

        } catch (Exception e) {
            log.warn("로그인 실패: {}", request.getEmail());
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    public UserResponseDTO.RefreshTokenResponse refreshToken(UserRequestDTO.RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // 리프레시 토큰에서 사용자 정보 추출
            Integer userId = jwtProvider.extractUserId(refreshToken);
            String email = jwtProvider.extractEmail(refreshToken);

            // 사용자 정보 조회 (UserDetailsService 대신 직접 조회)
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // UserDetails 객체 생성 (직접 생성)
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

            // 리프레시 토큰 검증
            if (jwtProvider.validateRefreshToken(refreshToken, userDetails, email, userId)) {
                // 새로운 액세스 토큰 생성
                String newAccessToken = jwtProvider.generateToken(user.getEmail(), user.getId());

                return UserResponseDTO.RefreshTokenResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken) // 기존 리프레시 토큰 유지
                        .build();
            } else {
                throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
            }

        } catch (Exception e) {
            log.error("토큰 갱신 실패: {}", e.getMessage());
            throw new IllegalArgumentException("토큰 갱신에 실패했습니다.");
        }
    }
}
