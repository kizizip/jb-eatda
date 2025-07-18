package com.jbeatda.domain.users.service;

import com.jbeatda.DTO.requestDTO.UserRequestDTO;
import com.jbeatda.DTO.responseDTO.UserResponseDTO;
import com.jbeatda.S3Service;
import com.jbeatda.config.JwtProvider;
import com.jbeatda.config.S3Config;
import com.jbeatda.domain.users.entity.User;
import com.jbeatda.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final S3Service s3Service;

    // 회원가입
    @Transactional
    public UserResponseDTO.SignUpResponse signUp(UserRequestDTO.SignUpRequest request, MultipartFile profileImage) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String profileImageUrl = null;

        // 프로필 이미지 디버깅
        System.out.println("=== 서비스 파일 디버깅 ===");
        System.out.println("profileImage null? " + (profileImage == null));
        if (profileImage != null) {
            System.out.println("파일명: " + profileImage.getOriginalFilename());
            System.out.println("파일 크기: " + profileImage.getSize());
            System.out.println("파일 비어있음? " + profileImage.isEmpty());
        }

        // 프로필 이미지가 있는 경우 S3에 업로드
        if (profileImage != null && !profileImage.isEmpty()) {
            // 이미지 파일 유효성 검사
            if (!isValidImageFile(profileImage)) {
                throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
            }

            if (profileImage.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다.");
            }

            try {
                profileImageUrl = s3Service.uploadFile(profileImage);
            } catch (IOException e) {
                log.error("프로필 이미지 업로드 실패: {}", e.getMessage());
                throw new RuntimeException("프로필 이미지 업로드에 실패했습니다.", e);
            }
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userName(request.getUserName())
                .profileImageUrl(profileImageUrl)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponseDTO.SignUpResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .userName(savedUser.getUserName())
                .profileImage(savedUser.getProfileImageUrl())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }


    // 이미지 파일 유효성 검사 메소드
    private boolean isValidImageFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) return false;

        String extension = originalFilename.toLowerCase();
        return extension.endsWith(".jpg") ||
                extension.endsWith(".jpeg") ||
                extension.endsWith(".png") ||
                extension.endsWith(".gif");
    }

    // 로그인
    public UserResponseDTO.LoginResponse login (UserRequestDTO.LoginRequest request) {
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

    // 로그아웃
    public UserResponseDTO.LogoutResponse logout(UserRequestDTO.LogoutRequest request) {
        try {
            // 현재 요청에서 Access Token 추출
            String accessToken = jwtProvider.getAccessTokenFromRequest();

            if (accessToken != null && !accessToken.isEmpty()) {
                // Access Token에서 사용자 정보 추출
                Integer userId = jwtProvider.extractUserId(accessToken);
                String email = jwtProvider.extractEmail(accessToken);

                // Access Token을 블랙리스트에 추가
                jwtProvider.addToAccessTokenBlacklist(accessToken);
                log.info("Access Token 블랙리스트 추가 완료");

                // Redis에서 Refresh Token 삭제
                jwtProvider.deleteRefreshToken(userId);

                return UserResponseDTO.LogoutResponse.builder()
                        .message("로그아웃이 성공적으로 처리되었습니다.")
                        .logoutTime(LocalDateTime.now())
                        .build();
            } else {
                throw new IllegalArgumentException("유효한 토큰이 없습니다.");
            }

        } catch (Exception e) {
            log.error("로그아웃 실패: {}", e.getMessage());
            throw new IllegalArgumentException("로그아웃 처리 중 오류가 발생했습니다.");
        }
    }

    // 회원 탈퇴
    @Transactional
    public UserResponseDTO.WithdrawalResponse withdrawal() {
        try {
            log.info("=== 회원 탈퇴 시도 ===");

            // 현재 요청에서 Access Token 추출하여 사용자 정보 획득
            String accessToken = jwtProvider.getAccessTokenFromRequest();
            if (accessToken == null || accessToken.isEmpty()) {
                throw new IllegalArgumentException("유효한 토큰이 없습니다.");
            }

            // 토큰에서 사용자 정보 추출
            Integer userId = jwtProvider.extractUserId(accessToken);
            String email = jwtProvider.extractEmail(accessToken);

            log.info("회원 탈퇴 요청 사용자: {} (ID: {})", email, userId);

            // 사용자 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // 탈퇴 시각과 이메일 미리 저장
            LocalDateTime withdrawalTime = LocalDateTime.now();
            String userEmail = user.getEmail();

            log.info("삭제할 연관 데이터 확인:");
            log.info("- 스탬프: {}개", user.getStamps().size());
            log.info("- 코스: {}개", user.getCourses().size());
            log.info("- 북마크: {}개", user.getBookmarks().size());

            try {
                // 연관 엔티티들이 CASCADE.ALL로 설정되어 있어서 자동으로 삭제됨
                userRepository.delete(user);
                log.info("사용자 및 연관 데이터 삭제 완료");

            } catch (Exception e) {
                log.error("사용자 데이터 삭제 중 오류: {}", e.getMessage(), e);

                // 구체적인 오류 메시지 제공
                if (e.getMessage().contains("foreign key constraint")) {
                    throw new RuntimeException("연관된 데이터로 인해 삭제할 수 없습니다. 관리자에게 문의하세요.");
                } else {
                    throw new RuntimeException("사용자 데이터 삭제에 실패했습니다: " + e.getMessage());
                }
            }

            // 토큰 정리 (사용자 삭제 후)
            try {
                jwtProvider.addToAccessTokenBlacklist(accessToken);
                log.info("Access Token 블랙리스트 추가 완료");
            } catch (Exception e) {
                log.warn("Access Token 블랙리스트 추가 실패: {}", e.getMessage());
            }

            try {
                jwtProvider.deleteRefreshToken(userId);
                log.info("Refresh Token 삭제 완료");
            } catch (Exception e) {
                log.warn("Refresh Token 삭제 실패: {}", e.getMessage());
            }

            return UserResponseDTO.WithdrawalResponse.builder()
                    .message("회원 탈퇴가 성공적으로 처리되었습니다.")
                    .email(userEmail)
                    .withdrawalTime(withdrawalTime)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("회원 탈퇴 실패 (클라이언트 오류): {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("회원 탈퇴 실패 (서버 오류): {}", e.getMessage(), e);
            throw new RuntimeException("회원 탈퇴 처리 중 서버 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
