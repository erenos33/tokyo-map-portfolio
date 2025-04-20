package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.favorite.FavoriteCheckResponseDto;
import me.tokyomap.dto.favorite.FavoriteRequestDto;
import me.tokyomap.dto.favorite.FavoriteRestaurantResponseDto;
import me.tokyomap.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "즐겨찾기 API", description = "Favorite API (등록/삭제/조회)")
@SecurityRequirement(name = "bearerAuth")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    @Operation(summary = "즐겨찾기 등록")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> addFavorite(
            @RequestBody @Valid FavoriteRequestDto requestDto,
            Authentication authentication
    ) {
        String email = authentication.getName();
        favoriteService.addFavorite(email, requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Operation(summary = "즐겨찾기 삭제")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> removeFavorite(
            @RequestBody @Valid FavoriteRequestDto requestDto,
            Authentication authentication
    ) {
        String email = authentication.getName();
        favoriteService.removeFavorite(email, requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    @Operation(summary = "특정 음식점 즐겨찾기 여부 확인")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FavoriteCheckResponseDto> checkFavorite(
            @RequestParam Long restaurantId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        FavoriteCheckResponseDto responseDto = favoriteService.checkFavorite(email, restaurantId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/my")
    @Operation(summary = "내 즐겨찾기 목록 조회 (페이징)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<FavoriteRestaurantResponseDto>> getMyFavorites(
            Authentication authentication,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) {
        String email = authentication.getName();
        Page<FavoriteRestaurantResponseDto> favorites = favoriteService.getMyFavorites(email, pageable);
        return ResponseEntity.ok(favorites);
    }

}
