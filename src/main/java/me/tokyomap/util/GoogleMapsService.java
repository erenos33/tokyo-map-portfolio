package me.tokyomap.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.tokyomap.dto.maps.GooglePlaceDetailResponseDto;
import me.tokyomap.dto.maps.GooglePlaceResponseDto;
import me.tokyomap.dto.maps.GooglePlaceDetailWrapper;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
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

    public Mono<String> searchPlaces(String keyword, String location) {
        return webClient.get()
                .uri(u -> u.path("/place/textsearch/json")
                        .queryParam("query", keyword + " in " + location)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

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

    public Mono<GooglePlaceDetailResponseDto> getPlaceDetail(String placeId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/place/details/json")
                        .queryParam("place_id", placeId)
                        .queryParam("fields", "formatted_address,formatted_phone_number,price_level,opening_hours")
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new CustomException(ErrorCode.GOOGLE_API_ERROR)))
                )
                .bodyToMono(GooglePlaceDetailWrapper.class)
                .map(wrapper -> {
                    GooglePlaceDetailResponseDto dto = wrapper.getResult();
                    if (dto == null) {
                        throw new CustomException(ErrorCode.GOOGLE_API_ERROR);
                    }
                    return dto;
                });
    }
}
