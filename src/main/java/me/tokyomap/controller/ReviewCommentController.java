package me.tokyomap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.tokyomap.dto.common.ApiResponse;
import me.tokyomap.dto.review.PageRequestDto;
import me.tokyomap.dto.review.ReviewCommentRequestDto;
import me.tokyomap.dto.review.ReviewCommentResponseDto;
import me.tokyomap.service.ReviewCommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "리뷰 댓글 API", description = "리뷰에 대한 댓글 등록 및 조회 기능 제공")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}/comments")
public class ReviewCommentController {

    private final ReviewCommentService reviewCommentService;

    @Operation(summary = "리뷰 댓글 등록", description = "로그인한 사용자가 리뷰에 댓글을 작성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ApiResponse<Void> createComment(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewCommentRequestDto dto,
            Authentication authentication
    ) {
        reviewCommentService.createComment(reviewId, dto, authentication.getName());
        return ApiResponse.success();
    }

    @Operation(summary = "리뷰 댓글 조회", description = "createdAt 기준 내림차순 정렬됩니다.")
    @GetMapping
    public ApiResponse<Page<ReviewCommentResponseDto>> getComments(
            @PathVariable Long reviewId,
            @Valid PageRequestDto pageDto
    ) {
        Pageable pageable = PageRequest.of(pageDto.page(), pageDto.size());
        return ApiResponse.success(reviewCommentService.getCommentsByReviewId(reviewId, pageable));
    }
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다. 작성자 본인만 수정할 수 있습니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{commentId}")
    public ApiResponse<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid ReviewCommentRequestDto dto,
            Authentication authentication
    ) {
        reviewCommentService.updateComment(commentId, dto, authentication.getName());
        return ApiResponse.success();
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다. 작성자 본인만 삭제할 수 있습니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        reviewCommentService.deleteComment(commentId, authentication.getName());
        return ApiResponse.success();
    }
}
