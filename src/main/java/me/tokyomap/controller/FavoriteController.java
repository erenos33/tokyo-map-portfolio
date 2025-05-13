package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.common.ApiResponse;
import me.tokyomap.dto.favorite.FavoriteCheckResponseDto;
import me.tokyomap.dto.favorite.FavoriteRequestDto;
import me.tokyomap.dto.favorite.FavoriteRestaurantResponseDto;
import me.tokyomap.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * お気に入り（Favorite）関連APIコントローラー
 * レストランのお気に入り登録、削除、確認、一覧取得を提供
 */
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "お気に入りAPI", description = "お気に入りの登録・削除・確認・一覧取得に関するAPI")
@SecurityRequirement(name = "bearerAuth")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * レストランをお気に入りに登録する
     */
    @PostMapping
    @Operation(
            summary = "お気に入り登録",
            description = "指定されたレストランをユーザーのお気に入りリストに追加します。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> addFavorite(
            @RequestBody @Valid FavoriteRequestDto requestDto,
            Authentication authentication
    ) {
        String email = authentication.getName();
        favoriteService.addFavorite(email, requestDto);
        return ApiResponse.success();
    }

    /**
     * お気に入りからレストランを削除する
     */
    @DeleteMapping
    @Operation(
            summary = "お気に入り削除",
            description = "ユーザーのお気に入りリストから指定されたレストランを削除します。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> removeFavorite(
            @RequestBody @Valid FavoriteRequestDto requestDto,
            Authentication authentication
    ) {
        String email = authentication.getName();
        favoriteService.removeFavorite(email, requestDto);
        return ApiResponse.success();
    }

    /**
     * 指定されたレストランがお気に入り登録済みか確認
     */
    @GetMapping("/check")
    @Operation(
            summary = "お気に入り登録の確認",
            description = "指定したレストランがユーザーのお気に入りに登録されているかを確認します。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<FavoriteCheckResponseDto> checkFavorite(
            @RequestParam Long restaurantId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        FavoriteCheckResponseDto responseDto = favoriteService.checkFavorite(email, restaurantId);
        return ApiResponse.success(responseDto);
    }

    /**
     * ユーザーのお気に入り一覧をページングで取得
     */
    @GetMapping("/my")
    @Operation(
            summary = "自分のお気に入り一覧取得",
            description = "現在ログインしているユーザーのお気に入りレストランをページング形式で取得します。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Page<FavoriteRestaurantResponseDto>> getMyFavorites(
            Authentication authentication,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) {
        String email = authentication.getName();
        Page<FavoriteRestaurantResponseDto> favorites = favoriteService.getMyFavorites(email, pageable);
        return ApiResponse.success(favorites);
    }
}
