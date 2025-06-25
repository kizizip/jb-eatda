package com.jbeatda.domain.stores.client;


import com.jbeatda.DTO.external.DoStoreDetailApiResponseDTO;
import com.jbeatda.DTO.external.DoStoreListApiResponseDTO;
import com.jbeatda.exception.ExternalApiException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Map;
import java.util.function.Function;

/**
 * 도지정향토음식점 정보 API
 */
@Component
@Slf4j
public class DoStoreApiClient {

    private final RestTemplate restTemplate;

    @Value("${do-store.api.base-url}")
    private String baseUrl;

    @Value("${do-store.api.service-key}")
    private String serviceKey;

    @Value("${do-store.api.endpoints.get-stores-list}")
    private String listEndpoint;

    @Value("${do-store.api.endpoints.get-stores-Detail}")
    private String detailEndpoint;

    public DoStoreApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 도지정향토음식점 정보 - 목록조회
    public List<DoStoreListApiResponseDTO.StoreItem>doStoreList(String area){
        return callApiAndParseResponse(
                listEndpoint,
                Map.of("Area", area, "_type", "json"),
                "특정 지역 식당 목록 API",
                xmlResponse -> parseStoreListXml(xmlResponse)
        );
    }

    // 도지정향토음식점 정보 - 상세조회
    public DoStoreDetailApiResponseDTO.StoreDetail doStoreDetail(String sno) {
        List<DoStoreDetailApiResponseDTO.StoreDetail> results = callApiAndParseResponse(
                detailEndpoint,
                Map.of("SNO", sno, "_type", "json"),
                "특정 식상 상세 API",
                xmlResponse -> parseStoreDetailXml(xmlResponse)
        );

        return results.isEmpty() ? null : results.get(0);
    }





    /**
     * 공통 API 호출 및 파싱 로직
     */
    private <T> T callApiAndParseResponse(
            String endpoint,
            Map<String, String> queryParams,
            String apiName,
            Function<String,T>xmlParser) {
        try{
            // 1. API URL 생성
            String url = buildApiUrl(endpoint, queryParams);
            log.info("{} 호출 시작 - params: {}", apiName, queryParams);

            // 2. API request 보내기
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

            // 3. 성공적으로 받아왔으면, body 부분 가져오기
            String xmlResponse = responseEntity.getBody();
            log.info("XML 응답 받음");

            // 4. XML 파싱
            T result = xmlParser.apply(xmlResponse);
            log.info("{} 호출 완료", apiName);

            return result;

        }catch (ResourceAccessException e) {
            log.error("API 타임아웃 또는 네트워크 오류 - {}, params: {}", apiName, queryParams, e);
            throw new ExternalApiException("네트워크 오류로 맛집 정보를 가져올 수 없습니다.");

        } catch (HttpServerErrorException e) {
            log.error("API 서버 오류 - {}, status: {}, params: {}", apiName, e.getStatusCode(), queryParams, e);
            throw new ExternalApiException("외부 서비스에 일시적인 문제가 발생했습니다.");

        } catch (ExternalApiException e) {
            throw e;

        } catch (Exception e) {
            log.error("예상치 못한 오류 발생 - {}, params: {}", apiName, queryParams, e);
            throw new ExternalApiException("맛집 정보 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * API URL 생성 (공통)
     */
    private String buildApiUrl(String endpoint, Map<String, String> queryParams){
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/" + endpoint)
                .queryParam("serviceKey", serviceKey);

        // 동적으로 쿼리 파라미터 추가
        queryParams.forEach(builder::queryParam);

        String url = builder.build().toUriString();
        log.info("요청 API URL: {}", url);
        return url;
    }

    /**
     * 매장 목록 XML 파싱
     */
    private List<DoStoreListApiResponseDTO.StoreItem> parseStoreListXml(String xmlResponse) {
        // 공통 XML 파싱 로직
        return parseXmlResponse(xmlResponse, "item", item ->
                DoStoreListApiResponseDTO.StoreItem.builder()
                        .sno(getTextContent(item, "SNO"))
                        .name(getTextContent(item, "NAME"))
                        .area(getTextContent(item, "AREA"))
                        .address(getTextContent(item, "ADDRESS"))
                        .tel(getTextContent(item, "TEL"))
                        .time(getTextContent(item, "TIME"))
                        .img(getTextContent(item, "IMG"))
                        .build()
        );
    }

    /**
     * 매장 상세 XML 파싱
     */
    private List<DoStoreDetailApiResponseDTO.StoreDetail> parseStoreDetailXml(String xmlResponse) {
        return parseXmlResponse(xmlResponse, "item", item ->
                DoStoreDetailApiResponseDTO.StoreDetail.builder()
                        .sno(getTextContent(item, "SNO"))
                        .name(getTextContent(item, "NAME"))
                        .area(getTextContent(item, "AREA"))
                        .address(getTextContent(item, "ADDRESS"))
                        .tel(getTextContent(item, "TEL"))
                        .time(getTextContent(item, "TIME"))
                        .img(getTextContent(item, "IMG"))
                        .seat(getTextContent(item, "SEAT"))
                        .holyday(getTextContent(item, "HOLYDAY"))
                        .park(getTextContent(item, "PARK"))
                        .car(getTextContent(item, "CAR"))
                        .etc(getTextContent(item, "ETC"))
                        .content(getTextContent(item, "CONTENT"))
                        .map(getTextContent(item, "MAP"))
                        .seq(getTextContent(item, "SEQ"))
                        .cktype(getTextContent(item, "CKTYPE"))
                        .food(getTextContent(item, "FOOD"))
                        .build()
        );
    }

    /**
     * 공통 XML 파싱 로직
     */
    private <T> List<T> parseXmlResponse(String xmlResponse, String itemTagName, Function<Element, T> itemMapper) {
        List<T> items = new ArrayList<>();

        try {

            // 1. XML 파서 팩토리 생성
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // 2. 실제 XML 파서(DocumentBuilder) 생성
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 3. XML 문자열을 Document 객체로 변환
            Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes()));

            //4. 특정 태그명을 가진 모든 요소 찾기
            NodeList itemNodes = doc.getElementsByTagName(itemTagName);
            log.info("XML에서 찾은 {} 개수: {}", itemTagName, itemNodes.getLength());

            // 6. 각 요소를 순회하면서 객체로 변환
            for (int i = 0; i < itemNodes.getLength(); i++) {
                Element item = (Element) itemNodes.item(i);
                T mappedItem = itemMapper.apply(item);
                items.add(mappedItem);
            }

        } catch (Exception e) {
            log.error("XML 파싱 실패", e);
        }

        return items;
    }

    /**
     * XML 요소에서 텍스트 내용 추출 (공통)
     */
    private String getTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            String content = nodeList.item(0).getTextContent();
            return content != null ? content.trim() : "";
        }
        return "";
    }






//    public List<DoStoreListApiResponseDTO.StoreItem> DoStoreList(String area) {
//
//        try {
//
//            String url = buildApiUrl(area);
//
//            log.info("전북 맛집 API 호출 시작 - area: {}", area);
//
//            ResponseEntity<String> responseEntity = restTemplate.exchange(
//                    url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
//
//            String xmlResponse = responseEntity.getBody();
//            log.info("XML 응답 받음");
//
//            List<DoStoreListApiResponseDTO.StoreItem> result = parseXmlResponse(xmlResponse);
//            log.info("최종 반환할 매장 수: {}", result.size());
//
//            return result;
//
//        } catch (ResourceAccessException e) {
//            log.error("API 타임아웃 또는 네트워크 오류 - area: {}", area, e);
//            throw new ExternalApiException("네트워크 오류로 맛집 정보를 가져올 수 없습니다.");
//
//        } catch (HttpServerErrorException e) {
//            log.error("API 서버 오류 - area: {}, status: {}", area, e.getStatusCode(), e);
//            throw new ExternalApiException("외부 서비스에 일시적인 문제가 발생했습니다.");
//
//        } catch (ExternalApiException e) {
//            throw e; // 이미 처리된 예외는 그대로 전파
//
//        } catch (Exception e) {
//            log.error("예상치 못한 오류 발생 - area: {}", area, e);
//            throw new ExternalApiException("맛집 정보 조회 중 오류가 발생했습니다.");
//        }
//    }
//
//
//    private String buildApiUrl(String area) {
//        String url = UriComponentsBuilder
//                .fromHttpUrl(doStoreListProperties.getBaseUrl() + "/" + doStoreListProperties.getEndpoints().get("get-stores-list"))
//                .queryParam("serviceKey", doStoreListProperties.getServiceKey())
//                .queryParam("Area", area)
//                .queryParam("_type", "json")
//                .build()
//                .toUriString();
//
//        log.info("요청 API URL: {}", url);
//        return url;
//    }
//
//    private List<DoStoreListApiResponseDTO.StoreItem> parseXmlResponse(String xmlResponse) {
//        List<DoStoreListApiResponseDTO.StoreItem> stores = new ArrayList<>();
//
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes()));
//
//            NodeList itemNodes = doc.getElementsByTagName("item");
//            log.info("XML에서 찾은 item 개수: {}", itemNodes.getLength());
//
//            for (int i = 0; i < itemNodes.getLength(); i++) {
//                Element item = (Element) itemNodes.item(i);
//
//                DoStoreListApiResponseDTO.StoreItem store = DoStoreListApiResponseDTO.StoreItem.builder()
//                        .sno(getTextContent(item, "SNO"))
//                        .name(getTextContent(item, "NAME"))
//                        .area(getTextContent(item, "AREA"))
//                        .address(getTextContent(item, "ADDRESS"))
//                        .tel(getTextContent(item, "TEL"))
//                        .time(getTextContent(item, "TIME"))
//                        .img(getTextContent(item, "IMG"))
//                        .build();
//
//
//                stores.add(store);
//            }
//
//        } catch (Exception e) {
//            log.error("XML 파싱 실패", e);
//        }
//
//        return stores;
//    }
//
//    private String getTextContent(Element parent, String tagName) {
//        NodeList nodeList = parent.getElementsByTagName(tagName);
//        if (nodeList.getLength() > 0) {
//            return nodeList.item(0).getTextContent();
//        }
//        return "";
//    }



}
