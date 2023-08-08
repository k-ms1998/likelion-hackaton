package com.example.lionproject.OpenApi;

import com.example.lionproject.OpenApi.CallResponse.Raw.ListPublicReservationEducationRaw;
import com.example.lionproject.OpenApi.CallResponse.Raw.ListPublicReservationMedicalRaw;
import com.example.lionproject.OpenApi.CallResponse.Raw.SenuriServiceRawResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class CallOpenApi {

    @Value("${api.key.seoul}")
    private String apiKey;

    @Value("${api.hyunsoo.key}")
    private String apiKeyHyunsoo;

    private final WebClient webClient;

    public ListPublicReservationMedicalRaw CallListPublicReservationMedical(Object startIndex, Object endIndex) {
        final String uri = String.format("http://openAPI.seoul.go.kr:8088/%s/xml/ListPublicReservationMedical/%d/%d/",
                apiKey, (Integer) startIndex, (Integer) endIndex);

        try {
            ListPublicReservationMedicalRaw response = webClient.get()
                    .uri(new URI(uri)) // 공공데이터는 URI 를 String 그대로 요청을 보내면 'SERVICE_KEY_IS_NOT_REGISTERED_ERROR' 오류 발생 -> URI 객체로 생성해서 요청 보내기
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .bodyToMono(ListPublicReservationMedicalRaw.class)
                    .single().blockOptional()
                    .orElse(new ListPublicReservationMedicalRaw(null, null, null));

            return response;

        } catch (Exception e) {
            throw new RuntimeException("[CallOpenApi] CallListPublicReservationMedical Error.");
        }
    }

    public ListPublicReservationEducationRaw CallListPublicReservationEducation(Object startIndex, Object endIndex) {
        final String uri = String.format("http://openAPI.seoul.go.kr:8088/%s/xml/ListPublicReservationEducation/%d/%d/",
                apiKey, (Integer) startIndex, (Integer) endIndex);

        try {
            ListPublicReservationEducationRaw response = webClient.get()
                    .uri(new URI(uri)) // 공공데이터는 URI 를 String 그대로 요청을 보내면 'SERVICE_KEY_IS_NOT_REGISTERED_ERROR' 오류 발생 -> URI 객체로 생성해서 요청 보내기
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .bodyToMono(ListPublicReservationEducationRaw.class)
                    .single().blockOptional()
                    .orElse(new ListPublicReservationEducationRaw(null, null, null));

            return response;

        } catch (Exception e) {
            throw new RuntimeException("[CallOpenApi] CallListPublicReservationEducation Error.");
        }
    }

    public SenuriServiceRawResponse CallSenuriService(int numOfRows, int pageNo) {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("http://apis.data.go.kr/");
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        //모든 값 요청.
        SenuriServiceRawResponse res = webClient.mutate() // mutate() => Return a builder to create a new WebClient whose settings are replicated from the current WebClient.
                .uriBuilderFactory(factory)
                .build()
                .get().uri(uriBuilder ->
                        uriBuilder.path("B552474/SenuriService/getJobList?ServiceKey={apiKey}&numOfRows={numOfRows}&pageNo={pageNo}")
                                .build(apiKeyHyunsoo, numOfRows, pageNo))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .retrieve()
                .bodyToMono(SenuriServiceRawResponse.class)
                .block();

        return res;
    }

}