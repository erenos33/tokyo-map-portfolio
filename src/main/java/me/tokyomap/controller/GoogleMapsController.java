package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.maps.GooglePlaceResponseDto;
import me.tokyomap.util.GoogleMapsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "Google Maps", description = "Google Maps 연동 장소 검색 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/maps")
public class GoogleMapsController {

    private final GoogleMapsService googleMapsService;

    @Operation(summary = "장소 검색 1페이지", description = "키워드와 위치로 1페이지 장소 검색을 수행합니다. lat/lng가 함께 전달되면 GPS 기반 검색으로 처리됩니다.")
    @GetMapping("/search")
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

    @Operation(summary = "다음 페이지 검색", description = "nextPageToken으로 다음 장소 페이지를 검색합니다. (2초 delay 포함)")
    @GetMapping("/next")
    public Mono<GooglePlaceResponseDto> searchNextPage(@RequestParam String token) {
        return googleMapsService.searchNextPage(token);
    }
}
