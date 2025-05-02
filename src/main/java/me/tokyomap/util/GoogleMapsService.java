package me.tokyomap.util;

import me.tokyomap.dto.maps.GooglePlaceResponseDto;
import org.springframework.beans.factory.annotation.Value;
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

    // 🔹 단순 텍스트 응답 테스트
    public Mono<String> searchPlaces(String keyword, String location) {
        return webClient.get()
                .uri(u -> u.path("/place/textsearch/json")
                        .queryParam("query", keyword + " in " + location)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    // 🔹 위치 기반 검색 (lat/lng 기반)
    public Mono<GooglePlaceResponseDto> searchByLocation(String keyword, double lat, double lng, int radius) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/place/textsearch/json")
                        .queryParam("query", keyword)
                        .queryParam("location", lat + "," + lng)
                        .queryParam("radius", radius)
                        .queryParam("type", "restaurant")
                        .queryParam("key", apiKey)
                        .build()
                )
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.createException().flatMap(Mono::error)
                )
                .bodyToMono(GooglePlaceResponseDto.class);
    }

    // 🔹 키워드 + 위치 기반 검색 (도시명 기반)
    public Mono<GooglePlaceResponseDto> searchFirstPage(String keyword, String location) {
        return webClient.get()
                .uri(u -> u.path("/place/textsearch/json")
                        .queryParam("query", keyword + " in " + location)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.createException().flatMap(Mono::error)
                )
                .bodyToMono(GooglePlaceResponseDto.class);
    }

    // 🔹 다음 페이지 검색
    public Mono<GooglePlaceResponseDto> searchNextPage(String nextPageToken) {
        return Mono.delay(Duration.ofSeconds(2))
                .flatMap(ignore -> webClient.get()
                        .uri(u -> u.path("/place/textsearch/json")
                                .queryParam("pagetoken", nextPageToken)
                                .queryParam("key", apiKey)
                                .build())
                        .retrieve()
                        .onStatus(
                                status -> status.isError(),
                                response -> response.createException().flatMap(Mono::error)
                        )
                        .bodyToMono(GooglePlaceResponseDto.class)
                );
    }
}
