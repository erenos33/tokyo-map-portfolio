package me.tokyomap.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                .uri(uriBuilder -> uriBuilder
                        .path("/place/textsearch/json")
                        .queryParam("query", keyword + " in " + location)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}
