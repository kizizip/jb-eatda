package com.jbeatda.domain.courses.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbeatda.DTO.internal.AiCourseRequestDTO;
import com.jbeatda.DTO.internal.StoreWithCoordinatesDTO;
import com.jbeatda.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OpenAiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${openai.api.model:gpt-3.5-turbo}")
    private String model;

    public OpenAiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * AI에게 코스 추천 요청
     */
    public String recommendCourse(AiCourseRequestDTO requestDTO) {
        try {
            // 1. 프롬프트 생성
            String prompt = buildCourseRecommendationPrompt(requestDTO);
            log.info("AI 코스 추천 요청 시작");

            // 2. OpenAI API 요청 바디 생성
            Map<String, Object> requestBody = buildRequestBody(prompt);

            // 3. HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 4. API 호출
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, String.class);

            // 5. 응답 파싱
            String aiResponse = parseOpenAiResponse(response.getBody());
            log.info("AI 코스 추천 완료");

            return aiResponse;

        } catch (ResourceAccessException e) {
            log.error("OpenAI API 타임아웃 또는 네트워크 오류", e);
            throw new ExternalApiException("AI 서비스 연결에 실패했습니다.");

        } catch (HttpClientErrorException e) {
            log.error("OpenAI API 클라이언트 오류 - status: {}", e.getStatusCode(), e);
            if (e.getStatusCode().value() == 401) {
                throw new ExternalApiException("AI 서비스 인증에 실패했습니다.");
            } else if (e.getStatusCode().value() == 429) {
                throw new ExternalApiException("AI 서비스 사용량 한도를 초과했습니다.");
            }
            throw new ExternalApiException("AI 서비스 요청 중 오류가 발생했습니다.");

        } catch (HttpServerErrorException e) {
            log.error("OpenAI API 서버 오류 - status: {}", e.getStatusCode(), e);
            throw new ExternalApiException("AI 서비스에 일시적인 문제가 발생했습니다.");

        } catch (Exception e) {
            log.error("예상치 못한 오류 발생", e);
            throw new ExternalApiException("AI 코스 추천 중 오류가 발생했습니다.");
        }
    }

    /**
     * 코스 추천 프롬프트 생성
     */
    private String buildCourseRecommendationPrompt(AiCourseRequestDTO requestDTO) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("전북 맛집 코스 추천\n");
        prompt.append("조건: ").append(String.join(",", requestDTO.getFoodStyles()));
        prompt.append(" | ").append(requestDTO.getTransportation());
        prompt.append(" | ").append(requestDTO.getDuration()).append("\n\n");

        // 매장 정보 (간단하게)
        prompt.append("매장목록:\n");
        for (int i = 0; i < requestDTO.getStores().size(); i++) {
            StoreWithCoordinatesDTO store = requestDTO.getStores().get(i);
            prompt.append(String.format("%d.%s|%s|%s|%s|%s|%s|%s|%s|%s,%s\n",
                    i + 1,
                    store.getStoreName(),
                    store.getSno(),
                    store.getStoreImage(),
                    store.getArea(),
                    store.getAddress(),
                    store.getMenu(),
                    store.getTime(),
                    store.getTel(),
                    store.getLatitude(),
                    store.getLongitude()));
        }

        // 요청사항 (간단하게)
        prompt.append("\n**중요:** 위 매장 정보를 정확히 복사 사용. 수정금지. 3-5개 선택, 효율적 동선 고려\n\n");

        // JSON 형식 (간단하게)
        prompt.append("JSON응답:\n");
        prompt.append("{\"courseName\":\"코스명\",\"description\":\"설명\",\"storeCount\":3,\"stores\":[{");
        prompt.append("\"storeName\":\"정확한매장명\",\"sno\":\"정확한SNO\",\"storeImage\":\"정확한이미지URL\",");
        prompt.append("\"area\":\"정확한지역\",\"address\":\"정확한주소\",\"smenu\":\"정확한메뉴\",");
        prompt.append("\"time\":\"정확한시간\",\"tel\":\"정확한전화\",\"visitOrder\":1,");
        prompt.append("\"lat\":\"정확한위도\",\"lng\":\"정확한경도\"}]}\n");
        prompt.append("위 매장정보 그대로 복사, visitOrder만 순서 변경");

        return prompt.toString();
    }

    /**
     * OpenAI API 요청 바디 생성
     */
    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("max_tokens", 2000);
        requestBody.put("temperature", 0.7);

        // messages 배열 생성
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        requestBody.put("messages", List.of(message));

        return requestBody;
    }

    /**
     * OpenAI API 응답 파싱
     */
    private String parseOpenAiResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode choices = root.get("choices");

            if (choices == null || choices.size() == 0) {
                throw new ExternalApiException("AI 응답을 파싱할 수 없습니다.");
            }

            JsonNode firstChoice = choices.get(0);
            JsonNode message = firstChoice.get("message");
            String content = message.get("content").asText();

            log.info("AI 응답 파싱 완료 - 응답 길이: {}", content.length());
            return content;

        } catch (Exception e) {
            log.error("OpenAI 응답 파싱 실패: {}", jsonResponse, e);
            throw new ExternalApiException("AI 응답을 처리하는 중 오류가 발생했습니다.");
        }
    }
}