package com.jbeatda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
/**
 * API 응답 타임 아웃
 *  -> 무한대기에 빠져서 서버가 터지는 걸 방지
 */
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);  // 5초 (밀리초)
        factory.setReadTimeout(10000);    // 10초 (밀리초)

        return new RestTemplate(factory);
    }
}