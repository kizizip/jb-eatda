package com.jbeatda.DTO.requestDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageRequestDTO {

    @NotNull(message = "page 값은 필수 입니다.")
    private int page;

}
