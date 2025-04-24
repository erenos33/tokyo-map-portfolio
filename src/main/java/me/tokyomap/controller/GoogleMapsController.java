package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import me.tokyomap.util.GoogleMapsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class GoogleMapsController {

    private final GoogleMapsService googleMapsService;

    @Operation(summary = "장소 검색 테스트 API", description = "구글 맵스 API를 이용해 키워드와 위치로 장소 검색 테스트를 수행합니다.")
    @GetMapping("/api/maps/search")
    public Mono<String> searchPlaces(
            @RequestParam String keyword,
            @RequestParam String location) {
        return googleMapsService.searchPlaces(keyword, location);
    }
}
