package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.common.ApiResponse;
import me.tokyomap.dto.review.ReviewLikeCountResponseDto;
import me.tokyomap.dto.review.ReviewRequestDto;
import me.tokyomap.dto.review.ReviewResponseDto;
import me.tokyomap.dto.review.ReviewStatisticsResponseDto;
import me.tokyomap.service.ReviewService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


/**
 * レビュー作成、編集、削除、いいね、統計などのAPIを提供
 */
@Tag(name = "レビューAPI", description = "レビューの投稿・編集・削除・統計・いいね関連のAPI")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * レビューを作成（ログイン必須）
     */
    @PostMapping("/reviews")
    @Operation(
            summary = "レビュー投稿",
            description = "ログイン中のユーザーが指定したレストランにレビューを投稿します。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<ReviewResponseDto> createReview(
            @Valid @RequestBody ReviewRequestDto dto,
            Authentication authentication
    ) {
        String email = authentication.getName();
        ReviewResponseDto response = reviewService.createReview(dto, email);
        return ApiResponse.success(response);
    }

    /**
     * レビューを編集（本人のみ）
     */
    @PutMapping("/reviews/{id}")
    @Operation(
            summary = "レビュー編集",
            description = "ユーザーが自身のレビューを編集し、編集結果を返します。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<ReviewResponseDto> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDto dto,
            Authentication authentication
    ) {
        String email = authentication.getName();
        ReviewResponseDto response = reviewService.updateReview(id, dto, email);
        return ApiResponse.success(response);
    }

    /**
     * レビューを削除（本人のみ）
     */
    @DeleteMapping("/reviews/{id}")
    @Operation(
            summary = "レビュー削除",
            description = "ユーザーが自身のレビューを削除します。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> deleteReview(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        reviewService.deleteReview(id, email);
        return ApiResponse.success();
    }

    /**
     * レストランのレビュー一覧を取得（ソート対応）
     */
    @GetMapping("/restaurants/{restaurantId}/reviews")
    @Operation(
            summary = "レビュー一覧取得（ソート対応）",
            description = "レビューを作成日時やいいね数などの条件で並べ替えて取得します。"
    )
    public ApiResponse<Page<ReviewResponseDto>> getReviewsSorted(
            @PathVariable Long restaurantId,
            @ParameterObject Pageable pageable
    ) {
        String sortProperty = "createdAt";
        Sort.Direction direction = Sort.Direction.DESC;

        for (Sort.Order order : pageable.getSort()) {
            sortProperty = order.getProperty();
            direction = order.getDirection();
        }

        Page<ReviewResponseDto> reviews = reviewService.getReviewsWithSort(restaurantId, sortProperty, direction, pageable);
        return ApiResponse.success(reviews);
    }

    /**
     * レビューにいいねを追加（ログイン必須）
     */
    @PostMapping("/reviews/{id}/like")
    @Operation(
            summary = "レビューにいいね",
            description = "レビューにいいねを追加します。ログインユーザーのみ可能です。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> likeReview(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        reviewService.likeReview(id, email);
        return ApiResponse.success();
    }

    /**
     * レビューのいいねを取り消し
     */
    @DeleteMapping("/reviews/{id}/like")
    @Operation(
            summary = "レビューのいいね取り消し",
            description = "レビューに追加したいいねを削除します。ログインユーザーのみ可能です。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> unlikeReview(@PathVariable Long id, @AuthenticationPrincipal String email) {
        reviewService.unlikeReview(id, email);
        return ApiResponse.success();
    }

    /**
     * レビューのいいね数を取得
     */
    @GetMapping("/reviews/{id}/likes/count")
    @Operation(
            summary = "レビューいいね数取得",
            description = "指定されたレビューの総いいね数を取得します。"
    )
    public ApiResponse<ReviewLikeCountResponseDto> getLikeCount(@PathVariable Long id) {
        return ApiResponse.success(reviewService.countLikesByReview(id));
    }

    /**
     * レストランのレビュー統計（平均評価・レビュー数）を取得
     */
    @GetMapping("/restaurants/{restaurantId}/reviews/statistics")
    @Operation(
            summary = "レビュー統計取得",
            description = "指定されたレストランの平均評価とレビュー件数を取得します。"
    )
    public ApiResponse<ReviewStatisticsResponseDto> getReviewStatistics(@PathVariable Long restaurantId) {
        return ApiResponse.success(reviewService.getReviewStatistics(restaurantId));
    }
}
