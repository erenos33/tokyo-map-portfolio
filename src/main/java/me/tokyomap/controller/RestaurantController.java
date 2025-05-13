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
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.service.RestaurantService;
import me.tokyomap.service.UserService;
import me.tokyomap.util.GoogleMapsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


/**
 * レストランに関する検索・登録・削除APIコントローラー
 */
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Tag(name = "レストランAPI", description = "レストラン検索、登録、削除などの操作を提供するAPI")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final GoogleMapsService googleMapsService;
    private final UserService userService;

    /**
     * カテゴリ、都市、営業中フィルターによるレストラン検索
     */
    @GetMapping("/search")
    @Operation(
            summary = "レストラン検索",
            description = "カテゴリ、地域、営業中フラグによってレストランを検索します。"
    )
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

    /**
     * IDを指定してレストラン詳細を取得
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "レストラン詳細取得",
            description = "指定されたIDでレストランの詳細情報を取得します。"
    )
    public ApiResponse<RestaurantSearchResponseDto> getRestaurantById(
            @Parameter(description = "レストランID", example = "1") @PathVariable Long id
    ) {
        RestaurantSearchResponseDto dto = restaurantService.getRestaurantById(id);
        return ApiResponse.success(dto);
    }

    /**
     * 緯度・経度・半径・キーワードを指定して周辺のレストラン検索
     */
    @GetMapping("/location")
    @Operation(
            summary = "位置情報でのレストラン検索（Google API）",
            description = "緯度・経度・半径・キーワードを指定して、周辺のレストランを検索します。"
    )
    public Mono<ApiResponse<GooglePlaceResponseDto>> searchByLocation(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int radius,
            @RequestParam String keyword) {

        return googleMapsService
                .searchByLocation(keyword, lat, lng, radius)
                .map(ApiResponse::success);
    }

    /**
     * next_page_tokenで次のページの検索結果を取得
     */
    @GetMapping("/location/next")
    @Operation(
            summary = "位置情報検索の次ページ取得（Google API）",
            description = "Google Places APIのnext_page_tokenを利用して次の検索結果を取得します。"
    )
    public Mono<ApiResponse<GooglePlaceResponseDto>> getNextPage(
            @RequestParam String token) {

        return googleMapsService
                .searchNextPage(token)
                .map(ApiResponse::success);
    }

    /**
     * Google Maps検索結果を自分のデータとして登録
     */
    @PostMapping("/register/google")
    @Operation(
            summary = "Google検索結果を登録",
            description = "Google Mapsで取得したレストランを登録します。ログインが必要です。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Long>> registerFromGoogle(
            @Valid @RequestBody GooglePlaceRegisterRequestDto dto,
            Authentication authentication) {

        String email = authentication.getName();
        Long restaurantId = restaurantService.registerGooglePlace(dto, email);
        return ResponseEntity.ok(ApiResponse.success(restaurantId));
    }

    /**
     * 自分が登録したレストランを取得（ページング付き）
     */
    @GetMapping("/my")
    @Operation(
            summary = "自分が登録したレストラン一覧取得"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Page<RestaurantSearchResponseDto>> getMyRestaurants(
            Authentication authentication,
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (authentication == null) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);  // or UNAUTHORIZED
        }

        String email = authentication.getName();

        if (!userService.isEmailVerified(email)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        Page<RestaurantSearchResponseDto> result = restaurantService.getMyRegisteredRestaurants(email, pageable);
        return ApiResponse.success(result);
    }

    /**
     * 自分が登録したレストランを削除
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "自分が登録したレストラン削除"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<String>> deleteMyRestaurant(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        restaurantService.deleteMyRestaurant(id, email);
        return ResponseEntity.ok(ApiResponse.success("削除完了"));
    }

    /**
     * 管理者によるレストラン削除API（全レストラン対象）
     */
    @DeleteMapping("/admin/restaurants/{id}")
    @Operation(
            summary = "管理者用レストラン削除",
            description = "管理者が任意のレストランを削除できるAPIです。ADMIN権限が必要です。"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteRestaurantAsAdmin(
            @Parameter(description = "レストランID", example = "1")
            @PathVariable Long id
    ) {
        restaurantService.deleteRestaurantByAdmin(id);
        return ApiResponse.success("削除完了");
    }

}
