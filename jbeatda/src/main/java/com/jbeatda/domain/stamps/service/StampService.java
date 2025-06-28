package com.jbeatda.domain.stamps.service;

import com.jbeatda.DTO.requestDTO.StampRequestDTO;
import com.jbeatda.DTO.responseDTO.StampResponseDTO;
import com.jbeatda.Mapper.AuthUtils;
import com.jbeatda.S3Service;
import com.jbeatda.domain.menus.entity.Menu;
import com.jbeatda.domain.menus.repository.MenuRepository;
import com.jbeatda.domain.stamps.entity.Stamp;
import com.jbeatda.domain.stamps.repository.StampRepository;
import com.jbeatda.domain.users.entity.User;
import com.jbeatda.domain.users.repository.UserRepository;
import com.jbeatda.exception.ApiResponseCode;
import com.jbeatda.exception.ApiResponseDTO;
import com.jbeatda.exception.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StampService {

    private final StampRepository stampRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final AuthUtils authUtils;
    private final S3Service s3Service;

    // 인증된 사용자의 전체 스탬프 조회
    public ApiResult getStampsByUser() {
        try {
            Integer userId = authUtils.getCurrentUserId();
            List<Stamp> stamps = stampRepository.findByUserId(userId);
            if (stamps.isEmpty()) {
                return ApiResponseDTO.fail(ApiResponseCode.NO_STAMPS_FOUND);
            }
            return StampResponseDTO.fromEntityList(stamps);
        } catch (Exception e) {
            log.error("getStampsByUser error", e);
            return ApiResponseDTO.fail(ApiResponseCode.SERVER_ERROR);
        }
    }

    // 인증된 사용자의 특정 메뉴에 대한 최신 스탬프 단건 조회
    public ApiResult getStampsByUserAndMenu(Integer menuId) {
        try {
            Integer userId = authUtils.getCurrentUserId();

            // menu 존재 확인
            if (!menuRepository.existsById(menuId)) {
                return ApiResponseDTO.fail(ApiResponseCode.NO_MENUS_FOUND);
            }

            // 최신 스탬프 1개 조회
            Optional<Stamp> latest = stampRepository
                    .findFirstByUserIdAndMenuIdOrderByCreatedAtDesc(userId, menuId);

            if (latest.isEmpty()) {
                return ApiResponseDTO.fail(ApiResponseCode.NO_STAMPS_FOUND);
            }

            return StampResponseDTO.fromEntity(latest.get());

        } catch (Exception e) {
            log.error("getStampsByUserAndMenu error", e);
            return ApiResponseDTO.fail(ApiResponseCode.SERVER_ERROR);
        }
    }


    // 인증된 사용자의 스탬프 등록
    public ApiResult createStamp(StampRequestDTO request, MultipartFile stampImage) {
        try {
            Integer userId = authUtils.getCurrentUserId();
            User user = userRepository.findFirstById(userId);
            if (user == null) {
                return ApiResponseDTO.fail(ApiResponseCode.NOT_FOUND_USER);
            }

            Menu menu = menuRepository.findFirstById(request.getMenuId());
            if (menu == null) {
                return ApiResponseDTO.fail(ApiResponseCode.NO_MENUS_FOUND);
            }

            String stampImageUrl = null;

            // 스탬프 이미지 디버깅
            System.out.println("=== 서비스 파일 디버깅 ===");
            System.out.println("profileImage null? " + (stampImage == null));
            if (stampImage != null) {
                System.out.println("파일명: " + stampImage.getOriginalFilename());
                System.out.println("파일 크기: " + stampImage.getSize());
                System.out.println("파일 비어있음? " + stampImage.isEmpty());
            }

            // 스탬프 이미지가 있는 경우 S3에 업로드
            if (stampImage != null && !stampImage.isEmpty()) {
                // 이미지 파일 유효성 검사
                if (!isValidImageFile(stampImage)) {
                    throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
                }

                if (stampImage.getSize() > 5 * 1024 * 1024) {
                    throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다.");
                }

                try {
                    stampImageUrl = s3Service.uploadFile(stampImage);
                } catch (IOException e) {
                    log.error("스탬프 이미지 업로드 실패: {}", e.getMessage());
                    throw new RuntimeException("프로필 이미지 업로드에 실패했습니다.", e);
                }
            }

            Stamp stamp = Stamp.builder()
                    .user(user)
                    .menu(menu)
                    .image(stampImageUrl)
                    .build(); // createdAt 자동 설정

            // 저장 및 DTO 변환
            Stamp saved = stampRepository.save(stamp);

            return StampResponseDTO.fromEntity(saved);

        } catch (Exception e) {
            log.error("createStamp error", e);
            return ApiResponseDTO.fail(ApiResponseCode.SERVER_ERROR);
        }


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
}
