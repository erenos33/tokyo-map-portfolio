package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.tokyomap.domain.review.entity.Review;
import me.tokyomap.domain.review.entity.ReviewComment;
import me.tokyomap.domain.review.repository.ReviewCommentRepository;
import me.tokyomap.domain.review.repository.ReviewRepository;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.dto.review.ReviewCommentRequestDto;
import me.tokyomap.dto.review.ReviewCommentResponseDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.mapper.ReviewCommentMapper;
import me.tokyomap.service.ReviewCommentService;
import me.tokyomap.util.EntityFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewCommentServiceImpl implements ReviewCommentService {

    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;



    @Override
    @Transactional
    public void createComment(Long reviewId, ReviewCommentRequestDto dto, String email) {

        log.info("리뷰 ID: {}, 유저 이메일: {}", reviewId, email);

        //유저 검증
        User user = EntityFinder.getUserOrThrow(userRepository, email);

        //리뷰 검증
        Review review = EntityFinder.getReviewOrThrow(reviewRepository, reviewId);

        //댓글 생성
        ReviewComment comment = ReviewComment.builder()
                .review(review)
                .user(user)
                .content(dto.content())
                .build();

        reviewCommentRepository.save(comment);
    }

    @Override
    public Page<ReviewCommentResponseDto> getCommentsByReviewId(Long reviewId, Pageable pageable) {
        EntityFinder.getReviewOrThrow(reviewRepository, reviewId);

        return reviewCommentRepository.findByReviewId(reviewId, pageable)
                .map(ReviewCommentMapper::toDto);
    }

    @Override
    @Transactional
    public void updateComment(Long commentId, ReviewCommentRequestDto dto, String email) {
        ReviewComment comment = EntityFinder.getCommentOrThrow(reviewCommentRepository, commentId);

        if (!comment.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
        }
        comment.setContent(dto.content());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String email) {
        ReviewComment comment = EntityFinder.getCommentOrThrow(reviewCommentRepository, commentId);

        if (!comment.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
        }

        reviewCommentRepository.delete(comment);
    }
}
