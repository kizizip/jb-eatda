package com.jbeatda.domain.stamps.service;

import com.jbeatda.DTO.requestDTO.StampRequestDTO;
import com.jbeatda.DTO.responseDTO.StampResponseDTO;
import com.jbeatda.Mapper.AuthUtils;
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
    public ApiResult createStamp(StampRequestDTO request) {
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

            Stamp stamp = Stamp.builder()
                    .user(user)
                    .menu(menu)
                    .image(request.getImage())
                    .build(); // createdAt 자동 설정

            // 저장 및 DTO 변환
            Stamp saved = stampRepository.save(Stamp.builder()
                    .user(user)
                    .menu(menuRepository.getReferenceById(request.getMenuId()))
                    .image(request.getImage())
                    .build());

            return StampResponseDTO.fromEntity(saved);

        } catch (Exception e) {
            log.error("createStamp error", e);
            return ApiResponseDTO.fail(ApiResponseCode.SERVER_ERROR);
        }
    }
}
