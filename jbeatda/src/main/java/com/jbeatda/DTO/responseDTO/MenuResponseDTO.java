package com.jbeatda.DTO.responseDTO;

import com.jbeatda.domain.menus.entity.Menu;
import com.jbeatda.exception.ApiResult;
import lombok.*;

        import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponseDTO implements ApiResult {

    //여러 메뉴 정보를 담는 리스트
    private List<MenuDTO> menus;

    // 개별 메뉴 정보를 담는 내부 클래스
    @Getter
    @Builder
    public static class MenuDTO {
        private Integer menuId;
        private String menuName;
        private Integer areaId;
    }

    // 메뉴 엔티티 리스트를 DTO 리스트로 변환하는 메서드
    // 여러 지역 정보를 한 번에 변환할 때 사용함
    public static MenuResponseDTO fromList(List<Menu> menuList) {
        List<MenuDTO> menuDTOs = menuList.stream()
                .map(menu -> {
                    return MenuDTO.builder()
                            .menuId(menu.getId())
                            .menuName(menu.getMenuName())
                            .areaId(menu.getArea().getId())
                            .build();
                    })
                .collect(Collectors.toList());

        return new MenuResponseDTO(menuDTOs);
    }
}

