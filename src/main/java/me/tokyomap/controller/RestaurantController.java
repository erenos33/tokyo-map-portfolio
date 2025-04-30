package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.common.ApiResponse;
import me.tokyomap.dto.maps.GooglePlaceResponseDto;
import me.tokyomap.dto.restaurant.GooglePlaceRegisterRequestDto;
import me.tokyomap.dto.restaurant.RestaurantSearchRequestDto;
import me.tokyomap.dto.restaurant.RestaurantSearchResponseDto;
import me.tokyomap.service.RestaurantService;
import me.tokyomap.util.GoogleMapsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant", description = "맛집 검색 및 조회 API")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final GoogleMapsService googleMapsService;

    @Operation(summary = "맛집 검색", description = "카테고리, 지역, 영업여부로 맛집을 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<Page<RestaurantSearchResponseDto>> searchRestaurants(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean openNow,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        RestaurantSearchRequestDto dto = new RestaurantSearchRequestDto(category, city, openNow);
        Page<RestaurantSearchResponseDto> result = restaurantService.searchRestaurants(dto, pageable);
        return ApiResponse.success(result);
    }

    @Operation(summary = "맛집 상세 조회", description = "ID로 맛집 상세정보를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<RestaurantSearchResponseDto> getRestaurantById(
            @Parameter(description = "음식점 ID", example = "1") @PathVariable Long id
    ) {
        RestaurantSearchResponseDto dto = restaurantService.getRestaurantById(id);
        return ApiResponse.success(dto);
    }

    @GetMapping("/location")
    @Operation(summary = "위치 기반 검색 (Google API)",
            description = "위도, 경도, 반경, 키워드를 기반으로 주변 맛집을 검색합니다.")
    public Mono<ApiResponse<GooglePlaceResponseDto>> searchByLocation(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int radius,
            @RequestParam String keyword) {

        return googleMapsService
                .searchByLocation(keyword, lat, lng, radius)
                .map(ApiResponse::success);
    }

    @GetMapping("/location/next")
    @Operation(summary = "위치 기반 검색 - 다음 페이지 (Google API)",
            description = "Google Places API의 next_page_token을 이용해 다음 페이지 결과를 조회합니다.")
    public Mono<ApiResponse<GooglePlaceResponseDto>> getNextPage(
            @RequestParam String token) {

        return googleMapsService
                .searchNextPage(token)
                .map(ApiResponse::success);
    }

    @Operation(summary = "Google 검색 결과 등록", description = "Google Maps에서 검색된 음식점을 등록", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/register/google")
    public ResponseEntity<ApiResponse<Long>> registerFromGoogle(
            @Valid @RequestBody GooglePlaceRegisterRequestDto dto,
            Authentication authentication) {

        String email = authentication.getName();
        Long restaurantId = restaurantService.registerGooglePlace(dto, email);
        return ResponseEntity.ok(ApiResponse.success(restaurantId));
    }

    @Operation(summary = "내가 등록한 음식점 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/my")
    public ApiResponse<Page<RestaurantSearchResponseDto>> getMyRestaurants(
            Authentication authentication,
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        String email = authentication.getName();
        Page<RestaurantSearchResponseDto> result = restaurantService.getMyRegisteredRestaurants(email, pageable);
        return ApiResponse.success(result);
    }

    @Operation(summary = "내가 등록한 음식점 삭제", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyRestaurant(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        restaurantService.deleteMyRestaurant(id, email);
        return ResponseEntity.noContent().build();
    }

}
