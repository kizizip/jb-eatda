package com.jbeatda.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "do-restaurant.api")
@Data
@Component

/**
 * 도지정향토음식점 정보 - 목록조회
 */
public class DoStoreListProperties {
    private String baseUrl;
    private String serviceKey;
    private Map<String, String> endpoints;
}
