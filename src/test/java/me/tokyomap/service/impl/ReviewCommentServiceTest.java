package me.tokyomap.service.impl;

import me.tokyomap.domain.review.entity.Review;
import me.tokyomap.domain.review.entity.ReviewComment;
import me.tokyomap.domain.review.repository.ReviewCommentRepository;
import me.tokyomap.domain.review.repository.ReviewRepository;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.dto.review.ReviewCommentRequestDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReviewCommentServiceTest {

    @Mock
    private ReviewCommentRepository commentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewCommentServiceImpl commentService;

    @Test
    void コメント作成_成功() {
        // given
        Long reviewId = 1L;
        String email = "user@example.com";
        ReviewCommentRequestDto dto = new ReviewCommentRequestDto("コメント内容");

        User user = mock(User.class);
        Review review = mock(Review.class);

        try (MockedStatic<me.tokyomap.util.EntityFinder> finder = mockStatic(me.tokyomap.util.EntityFinder.class)) {
            finder.when(() -> me.tokyomap.util.EntityFinder.getUserOrThrow(userRepository, email)).thenReturn(user);
            finder.when(() -> me.tokyomap.util.EntityFinder.getReviewOrThrow(reviewRepository, reviewId)).thenReturn(review);

            // when
            commentService.createComment(reviewId, dto, email);

            // then
            verify(commentRepository).save(any(ReviewComment.class));
        }
    }

    @Test
    void コメント一覧取得_成功() {
        // given
        Long reviewId = 1L;
        Review review = mock(Review.class);
        ReviewComment comment = mock(ReviewComment.class);
        Pageable pageable = Pageable.unpaged();
        Page<ReviewComment> page = new PageImpl<>(List.of(comment));

        try (MockedStatic<me.tokyomap.util.EntityFinder> finder = mockStatic(me.tokyomap.util.EntityFinder.class);
             MockedStatic<me.tokyomap.mapper.ReviewCommentMapper> mapper = mockStatic(me.tokyomap.mapper.ReviewCommentMapper.class)) {

            finder.when(() -> me.tokyomap.util.EntityFinder.getReviewOrThrow(reviewRepository, reviewId)).thenReturn(review);
            mapper.when(() -> me.tokyomap.mapper.ReviewCommentMapper.toDto(any(), any())).thenReturn(null);

            Authentication auth = mock(Authentication.class);
            when(auth.getName()).thenReturn("user@example.com");
            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(context);

            when(commentRepository.findByReviewId(reviewId, pageable)).thenReturn(page);

            // when
            commentService.getCommentsByReviewId(reviewId, pageable);

            // then
            verify(commentRepository).findByReviewId(reviewId, pageable);
        }
    }

    @Test
    void コメント更新_他人のコメント_失敗() {
        // given
        Long commentId = 1L;
        String email = "hacker@example.com";
        ReviewCommentRequestDto dto = new ReviewCommentRequestDto("書き換えたい");

        ReviewComment comment = mock(ReviewComment.class);
        User author = mock(User.class);
        when(comment.getUser()).thenReturn(author);
        when(author.getEmail()).thenReturn("author@example.com");

        try (MockedStatic<me.tokyomap.util.EntityFinder> finder = mockStatic(me.tokyomap.util.EntityFinder.class)) {
            finder.when(() -> me.tokyomap.util.EntityFinder.getCommentOrThrow(commentRepository, commentId)).thenReturn(comment);

            // when & then
            assertThatThrownBy(() -> commentService.updateComment(commentId, dto, email))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS.getMessage());
        }
    }

    @Test
    void コメント削除_成功() {
        // given
        Long commentId = 1L;
        String email = "owner@example.com";
        ReviewComment comment = mock(ReviewComment.class);
        User user = mock(User.class);
        when(comment.getUser()).thenReturn(user);
        when(user.getEmail()).thenReturn(email);

        try (MockedStatic<me.tokyomap.util.EntityFinder> finder = mockStatic(me.tokyomap.util.EntityFinder.class)) {
            finder.when(() -> me.tokyomap.util.EntityFinder.getCommentOrThrow(commentRepository, commentId)).thenReturn(comment);

            // when
            commentService.deleteComment(commentId, email);

            // then
            verify(commentRepository).delete(comment);
        }
    }
}
