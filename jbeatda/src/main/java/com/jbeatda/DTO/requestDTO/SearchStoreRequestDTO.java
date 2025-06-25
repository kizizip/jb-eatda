package com.jbeatda.DTO.requestDTO;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchStoreRequestDTO {

    @NotNull(message = "keyword 값은 필수 입니다.")
    private String keyword;

    @NotNull(message = "keyword 값은 필수 입니다.")
    private String area;


}
