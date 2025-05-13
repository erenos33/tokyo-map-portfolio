package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.maps.GooglePlaceDetailResponseDto;
import me.tokyomap.dto.maps.GooglePlaceResponseDto;
import me.tokyomap.util.GoogleMapsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Google Mapsとの連携によるレストラン検索API
 */
@Tag(name = "Google Maps", description = "Google Maps連携のスポット検索API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/maps")
public class GoogleMapsController {

    private final GoogleMapsService googleMapsService;

    /**
     * キーワードと位置情報を用いてスポット検索（初回ページ）
     */
    @GetMapping("/search")
    @Operation(
            summary = "スポット検索（1ページ目）",
            description = "キーワードと位置情報（オプション）を使用して、スポットの初回検索を行います。lat/lngが指定されている場合はGPSベースで検索されます。"
    )
    public Mono<GooglePlaceResponseDto> searchFirstPage(
            @RequestParam String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng
    ) {
        if (lat != null && lng != null) {
            return googleMapsService.searchByLocation(keyword, lat, lng, 3000);
        } else {
            return googleMapsService.searchFirstPage(keyword, location);
        }
    }

    /**
     * nextPageTokenを利用して次のスポットリストを取得
     */
    @GetMapping("/next")
    @Operation(
            summary = "次のページ検索",
            description = "nextPageTokenを指定して、スポット検索の2ページ目以降を取得します。（2秒ディレイ有り）"
    )
    public Mono<GooglePlaceResponseDto> searchNextPage(@RequestParam String token) {
        return googleMapsService.searchNextPage(token);
    }

    /**
     * placeIdを指定してスポットの詳細情報を取得
     */
    @GetMapping("/detail")
    @Operation(
            summary = "スポット詳細取得",
            description = "placeIdを指定して、Google Mapsのスポット詳細情報を取得します。"
    )
    public Mono<GooglePlaceDetailResponseDto> getPlaceDetail(@RequestParam String placeId) {
        return googleMapsService.getPlaceDetail(placeId);
    }
}
