package me.tokyomap.service;

import me.tokyomap.dto.review.ReviewCommentRequestDto;
import me.tokyomap.dto.review.ReviewCommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewCommentService {
    void createComment(Long reviewId, ReviewCommentRequestDto dto, String email);
    Page<ReviewCommentResponseDto> getCommentsByReviewId(Long reviewId, Pageable pageable);
    void updateComment(Long commentId, ReviewCommentRequestDto dto, String email);
    void deleteComment(Long commentId, String email);
}
