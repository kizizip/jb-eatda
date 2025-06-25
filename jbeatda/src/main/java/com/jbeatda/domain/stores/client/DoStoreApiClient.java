package com.jbeatda.domain.stores.client;


import com.jbeatda.DTO.external.DoStoreListApiResponseDTO;
import com.jbeatda.exception.ExternalApiException;
import com.jbeatda.properties.DoStoreListProperties;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 도지정향토음식점 정보 API
 */
@Component
@Slf4j
@AllArgsConstructor
public class DoStoreApiClient {

    private final DoStoreListProperties doStoreListProperties;
    private final RestTemplate restTemplate;

    // 도지정향토음식점 정보 - 목록조회
    public List<DoStoreListApiResponseDTO.StoreItem> DoStoreList(String area) {

        try {

            String url = buildApiUrl(area);

            log.info("전북 맛집 API 호출 시작 - area: {}", area);

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

            String xmlResponse = responseEntity.getBody();
            log.info("XML 응답 받음");

            List<DoStoreListApiResponseDTO.StoreItem> result = parseXmlResponse(xmlResponse);
            log.info("최종 반환할 매장 수: {}", result.size());

            return result;

        } catch (ResourceAccessException e) {
            log.error("API 타임아웃 또는 네트워크 오류 - area: {}", area, e);
            throw new ExternalApiException("네트워크 오류로 맛집 정보를 가져올 수 없습니다.");

        } catch (HttpServerErrorException e) {
            log.error("API 서버 오류 - area: {}, status: {}", area, e.getStatusCode(), e);
            throw new ExternalApiException("외부 서비스에 일시적인 문제가 발생했습니다.");

        } catch (ExternalApiException e) {
            throw e; // 이미 처리된 예외는 그대로 전파

        } catch (Exception e) {
            log.error("예상치 못한 오류 발생 - area: {}", area, e);
            throw new ExternalApiException("맛집 정보 조회 중 오류가 발생했습니다.");
        }
    }


    private String buildApiUrl(String area) {
        String url = UriComponentsBuilder
                .fromHttpUrl(doStoreListProperties.getBaseUrl() + "/" + doStoreListProperties.getEndpoints().get("get-stores-list"))
                .queryParam("serviceKey", doStoreListProperties.getServiceKey())
                .queryParam("Area", area)
                .queryParam("_type", "json")
                .build()
                .toUriString();

        log.info("요청 API URL: {}", url);
        return url;
    }

    private List<DoStoreListApiResponseDTO.StoreItem> parseXmlResponse(String xmlResponse) {
        List<DoStoreListApiResponseDTO.StoreItem> stores = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes()));

            NodeList itemNodes = doc.getElementsByTagName("item");
            log.info("XML에서 찾은 item 개수: {}", itemNodes.getLength());

            for (int i = 0; i < itemNodes.getLength(); i++) {
                Element item = (Element) itemNodes.item(i);

                DoStoreListApiResponseDTO.StoreItem store = DoStoreListApiResponseDTO.StoreItem.builder()
                        .sno(getTextContent(item, "SNO"))
                        .name(getTextContent(item, "NAME"))
                        .area(getTextContent(item, "AREA"))
                        .address(getTextContent(item, "ADDRESS"))
                        .tel(getTextContent(item, "TEL"))
                        .time(getTextContent(item, "TIME"))
                        .img(getTextContent(item, "IMG"))
                        .build();


                stores.add(store);
            }

        } catch (Exception e) {
            log.error("XML 파싱 실패", e);
        }

        return stores;
    }

    private String getTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }


}
