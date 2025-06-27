package com.jbeatda.DTO.responseDTO;

import com.jbeatda.domain.menus.entity.Menu;
import com.jbeatda.exception.ApiResult;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuOnlyResponseDTO implements ApiResult {

    private List<MenuOnlyDTO> menus;

    @Getter
    @Builder
    public static class MenuOnlyDTO {
        private Integer menuId;
        private String menuName;
    }

    public static MenuOnlyResponseDTO fromList(List<Menu> menuList) {
        List<MenuOnlyDTO> dtoList = menuList.stream()
                .map(menu -> MenuOnlyDTO.builder()
                        .menuId(menu.getId())
                        .menuName(menu.getMenuName())
                        .build())
                .collect(Collectors.toList());

        return new MenuOnlyResponseDTO(dtoList);
    }
}
