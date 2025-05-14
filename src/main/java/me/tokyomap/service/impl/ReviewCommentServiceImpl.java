package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * レビューコメントに関するサービスの実装クラス
 * コメントの作成・取得・更新・削除を担当
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewCommentServiceImpl implements ReviewCommentService {

    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    /**
     * 指定されたレビューにコメントを投稿する
     */
    @Override
    @Transactional
    public void createComment(Long reviewId, ReviewCommentRequestDto dto, String email) {

        User user = EntityFinder.getUserOrThrow(userRepository, email);

        Review review = EntityFinder.getReviewOrThrow(reviewRepository, reviewId);

        ReviewComment comment = ReviewComment.builder()
                .review(review)
                .user(user)
                .content(dto.content())
                .build();

        reviewCommentRepository.save(comment);
    }

    /**
     * 指定されたレビューIDに紐づくコメント一覧を取得（ページング対応）
     * 現在のログインユーザーがコメントの作成者かどうかも含めて返す
     */
    @Override
    public Page<ReviewCommentResponseDto> getCommentsByReviewId(Long reviewId, Pageable pageable) {
        EntityFinder.getReviewOrThrow(reviewRepository, reviewId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication != null ? authentication.getName() : "";

        return reviewCommentRepository.findByReviewId(reviewId, pageable)
                .map(comment -> ReviewCommentMapper.toDto(comment, currentEmail));
    }

    /**
     * 自分が投稿したコメントを修正する（他人のコメントにはアクセス不可）
     */
    @Override
    @Transactional
    public void updateComment(Long commentId, ReviewCommentRequestDto dto, String email) {
        ReviewComment comment = EntityFinder.getCommentOrThrow(reviewCommentRepository, commentId);

        if (!comment.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
        }
        comment.setContent(dto.content());
    }

    /**
     * 自分が投稿したコメントを削除する（他人のコメントにはアクセス不可）
     */
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
