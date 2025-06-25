package com.jbeatda.domain.stores.client;


import com.fasterxml.jackson.databind.JsonNode;
import com.jbeatda.exception.ExternalApiException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
@Slf4j

/**
 * 카카오 주소 -> 좌표 변환 api
 */
public class KakaoClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kakao.api.rest-key}")
    private String restApiKey;


    public KakaoClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    public List<String> getPoint(String address){

        try{
            // 1. API URL 생성
            String url = buildApiUrl(address);
            log.info("카카오 좌표변환 API 호출 시작 - address: {}", address);

            // 2. HTTP 헤더 설정 (Authorization 헤더 필수)
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + restApiKey);

            // 3. API 호출
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);

            // 4. JSON 응답 파싱
            String jsonResponse = response.getBody();
            log.info("카카오 API 응답 받음");

            // 5. 좌표 추출
            List<String> coordinates = parseCoordinates(jsonResponse);
            log.info("좌표 변환 완료 - address: {}, coordinates: {}", address, coordinates);

            return coordinates;

        }catch (ResourceAccessException e) {
            log.error("카카오 API 타임아웃 또는 네트워크 오류 - address: {}", address, e);
            throw new ExternalApiException("네트워크 오류로 좌표 변환을 할 수 없습니다.");

        } catch (HttpClientErrorException e) {
            log.error("카카오 API 클라이언트 오류 - address: {}, status: {}", address, e.getStatusCode(), e);
            if (e.getStatusCode().value() == 401) {
                throw new ExternalApiException("카카오 API 인증에 실패했습니다.");
            } else if (e.getStatusCode().value() == 400) {
                throw new ExternalApiException("잘못된 주소 형식입니다.");
            }
            throw new ExternalApiException("주소 검색 중 오류가 발생했습니다.");

        } catch (HttpServerErrorException e) {
            log.error("카카오 API 서버 오류 - address: {}, status: {}", address, e.getStatusCode(), e);
            throw new ExternalApiException("카카오 서비스에 일시적인 문제가 발생했습니다.");

        } catch (Exception e) {
            log.error("예상치 못한 오류 발생 - address: {}", address, e);
            throw new ExternalApiException("좌표 변환 중 오류가 발생했습니다.");
        }
    }

    /**
     * API URL 생성 (공통)
     */
    private String buildApiUrl( String address){

        String url = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/search/address")
                .queryParam("query", address)
                .build()
                .toUriString();

        log.info("요청 API URL: {}", url);
        return url;
    }
    /**
     *  JSON 응답에서 좌표 추출
     */
    private List<String> parseCoordinates(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode documents = root.get("documents");

            // 검색 결과가 없는 경우
            if (documents == null || documents.size() == 0) {
                log.warn("주소 검색 결과가 없습니다");
                throw new ExternalApiException("해당 주소를 찾을 수 없습니다.");
            }

            // 첫 번째 검색 결과의 좌표 추출 (documents 배열의 최상위 레벨에서)
            JsonNode firstResult = documents.get(0);

            // x, y 좌표 추출 (문자열로 되어있어서 asDouble()로 변환)
            String latitude = firstResult.get("y").asText();  // 위도
            String longitude = firstResult.get("x").asText(); // 경도

            log.info("좌표 추출 완료 - latitude: {}, longitude: {}", latitude, longitude);
            return Arrays.asList(latitude, longitude);

        } catch (Exception e) {
            log.error("JSON 파싱 실패: {}", jsonResponse, e);
            throw new ExternalApiException("좌표 정보 파싱 중 오류가 발생했습니다.");
        }
    }
}