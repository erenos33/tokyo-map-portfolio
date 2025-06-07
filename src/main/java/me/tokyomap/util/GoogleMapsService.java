package me.tokyomap.util;

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

/**
 * Google Maps APIとの連携を行うユーティリティサービスクラス
 * テキスト検索・位置検索・詳細情報取得・ページング対応などを提供
 */
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

    /**
     * キーワードと都市名からプレーンなテキスト検索を実行する
     * 応答は生のJSON文字列として返される（デバッグ用など）
     */
    public Mono<String> searchPlaces(String keyword, String location) {
        return webClient.get()
                .uri(u -> u.path("/place/textsearch/json")
                        .queryParam("query", keyword + " in " + location)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * 緯度・経度・検索半径・キーワードを指定して、Google Maps上でレストランを検索する
     */
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

    /**
     * 都市名とキーワードによる最初のページの検索結果を取得する
     */
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

    /**
     * next_page_tokenを利用して次のページの検索結果を取得する（2秒遅延付き）
     */
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

    /**
     * Google Place IDを指定して、詳細情報（住所・電話・価格帯・営業時間）を取得する
     */
    public Mono<GooglePlaceDetailResponseDto> getPlaceDetail(String placeId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/place/details/json")
                        .queryParam("place_id", placeId)
                        .queryParam("fields", "formatted_address,formatted_phone_number,price_level,opening_hours")
                        .queryParam("key", apiKey)
                        .queryParam("language", "ja")
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
