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


/**
 * レビューコメントに関する登録・取得・編集・削除API
 */
@Tag(name = "レビューコメントAPI", description = "レビューに対するコメントの登録、取得、編集、削除を提供するAPI")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}/comments")
public class ReviewCommentController {

    private final ReviewCommentService reviewCommentService;

    /**
     * レビューにコメントを登録（ログイン必須）
     */
    @PostMapping
    @Operation(
            summary = "コメント登録",
            description = "ログインしているユーザーが指定したレビューにコメントを投稿します。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> createComment(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewCommentRequestDto dto,
            Authentication authentication
    ) {
        reviewCommentService.createComment(reviewId, dto, authentication.getName());
        return ApiResponse.success();
    }

    /**
     * レビューに紐づくコメント一覧を取得（createdAt降順）
     */
    @GetMapping
    @Operation(
            summary = "コメント一覧取得",
            description = "レビューに対するコメントを作成日時の降順で取得します。"
    )
    public ApiResponse<Page<ReviewCommentResponseDto>> getComments(
            @PathVariable Long reviewId,
            @Valid PageRequestDto pageDto
    ) {
        Pageable pageable = PageRequest.of(pageDto.page(), pageDto.size());
        return ApiResponse.success(reviewCommentService.getCommentsByReviewId(reviewId, pageable));
    }

    /**
     * コメントを編集（本人のみ）
     */
    @PutMapping("/{commentId}")
    @Operation(
            summary = "コメント編集",
            description = "指定されたコメントを編集します（作成者本人のみ編集可能）。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid ReviewCommentRequestDto dto,
            Authentication authentication
    ) {
        reviewCommentService.updateComment(commentId, dto, authentication.getName());
        return ApiResponse.success();
    }

    /**
     * コメントを削除（本人のみ）
     */
    @DeleteMapping("/{commentId}")
    @Operation(
            summary = "コメント削除",
            description = "指定されたコメントを削除します（作成者本人のみ削除可能）。"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        reviewCommentService.deleteComment(commentId, authentication.getName());
        return ApiResponse.success();
    }
}
