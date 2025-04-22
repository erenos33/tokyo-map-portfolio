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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Review", description = "리뷰 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "로그인한 사용자가 음식점에 리뷰를 작성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/reviews")
    public ApiResponse<ReviewResponseDto> createReview(
            @Valid @RequestBody ReviewRequestDto dto,
            Authentication authentication) {

        //서비스 호출
        String email = authentication.getName();
        ReviewResponseDto response = reviewService.createReview(dto, email);
        return ApiResponse.success(response);
    }

    @Operation(summary = "리뷰 수정", description = "리뷰를 수정하고, 수정된 정보를 반환합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/reviews/{id}")
    public ApiResponse<ReviewResponseDto> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDto dto,
            Authentication authentication) {

        String email = authentication.getName();
        ReviewResponseDto response = reviewService.updateReview(id, dto, email);
        return ApiResponse.success(response);
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다. 작성자 본인만 삭제할 수 있습니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/reviews/{id}")
    public ApiResponse<Void> deleteReview(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        reviewService.deleteReview(id, email);
        return ApiResponse.success();
    }

    @Operation(summary = "음식점 리뷰 조회 (정렬 지원)", description = "정렬 조건에 따라 리뷰 목록을 조회합니다. createdAt 또는 likeCount 정렬 가능")
    @GetMapping("/restaurants/{restaurantId}/reviews")
    public ApiResponse<Page<ReviewResponseDto>> getReviewsSorted(
            @PathVariable Long restaurantId,
            @ParameterObject Pageable pageable) {

        String sortProperty = "createdAt";
        Sort.Direction direction = Sort.Direction.DESC;

        for (Sort.Order order : pageable.getSort()) {
            sortProperty = order.getProperty();
            direction = order.getDirection();
        }

        Page<ReviewResponseDto> reviews = reviewService.getReviewsWithSort(restaurantId, sortProperty, direction, pageable);
        return ApiResponse.success(reviews);
    }


    @Operation(summary = "리뷰 좋아요", description = "리뷰에 좋아요를 누릅니다. 로그인한 사용자만 가능합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/reviews/{id}/like")
    public ApiResponse<Void> likeReview(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();// 로그인 유저 이메일 추출
        reviewService.likeReview(id, email);
        return ApiResponse.success();// 바디 없음
    }

    @Operation(summary = "리뷰 좋아요 취소", description = "리뷰에 좋아요를 취소합니다. 로그인한 사용자만 가능합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/reviews/{id}/like")
    public ApiResponse<Void> unlikeReview(@PathVariable Long id, @AuthenticationPrincipal String email) {
        reviewService.unlikeReview(id, email);
        return ApiResponse.success();
    }

    @Operation(summary = "리뷰 좋아요 수 조회", description = "리뷰의 총 좋아요 개수를 반환합니다.")
    @GetMapping("/reviews/{id}/likes/count")
    public ApiResponse<ReviewLikeCountResponseDto> getLikeCount(@PathVariable Long id) {
        return ApiResponse.success(reviewService.countLikesByReview(id));
    }

    @Operation(summary = "리뷰 통계 조회", description = "해당 음식점의 평균 별점과 총 리뷰 수를 조회합니다.")
    @GetMapping("/restaurants/{restaurantId}/reviews/statistics")
    public ApiResponse<ReviewStatisticsResponseDto> getReviewStatistics(@PathVariable Long restaurantId) {
        return ApiResponse.success(reviewService.getReviewStatistics(restaurantId));
    }

    //이후 단계에서 각 메서드 추가(POST, PUT, DELETE, GET)
}
