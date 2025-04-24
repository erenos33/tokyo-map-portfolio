package me.tokyomap.util;

import me.tokyomap.dto.maps.GooglePlaceResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class GoogleMapsService {

    private final WebClient webClient;

    @Value("${google.maps.api.key}")
    private String apiKey;

    public GoogleMapsService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://maps.googleapis.com/maps/api")
                .build();
    }

    // 🔹 텍스트 응답 테스트용
    public Mono<String> searchPlaces(String keyword, String location) {
        return webClient.get()
                .uri(u -> u.path("/place/textsearch/json")
                        .queryParam("query", keyword + " in " + location)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    // 🔹 1페이지 검색 (예외 처리 포함)
    public Mono<GooglePlaceResponseDto> searchFirstPage(String keyword, String location) {
        return webClient.get()
                .uri(u -> u.path("/place/textsearch/json")
                        .queryParam("query", keyword + " in " + location)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status.isError(),                          // ← 이렇게 바꿔주세요
                        response -> response.createException().flatMap(Mono::error)
                )
                .bodyToMono(GooglePlaceResponseDto.class);
    }

    // 🔹 다음 페이지 검색 (예외 처리 포함)
    public Mono<GooglePlaceResponseDto> searchNextPage(String nextPageToken) {
        return Mono.delay(Duration.ofSeconds(2))
                .flatMap(ignore -> webClient.get()
                        .uri(u -> u.path("/place/textsearch/json")
                                .queryParam("pagetoken", nextPageToken)
                                .queryParam("key", apiKey)
                                .build())
                        .retrieve()
                        .onStatus(
                                status -> status.isError(),                     // ← 동일하게 수정
                                response -> response.createException().flatMap(Mono::error)
                        )
                        .bodyToMono(GooglePlaceResponseDto.class)
                );
    }
}
