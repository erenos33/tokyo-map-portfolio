package me.tokyomap.service.impl;

import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.restaurant.repository.RestaurantRepository;
import me.tokyomap.domain.review.entity.Review;
import me.tokyomap.domain.review.repository.ReviewRepository;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.dto.review.ReviewRequestDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.mapper.ReviewMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void レビュー投稿_成功() {
        // given
        String email = "user@example.com";
        Long restaurantId = 1L;

        User user = mock(User.class);
        Restaurant restaurant = mock(Restaurant.class);
        Review review = Review.builder()
                .user(user)
                .restaurant(restaurant)
                .content("美味しかった！")
                .rating(5)
                .build();

        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setRestaurantId(restaurantId);
        dto.setContent("美味しかった！");
        dto.setRating(5);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(reviewRepository.save(Mockito.<Review>any())).willReturn(review); // 修正した部分

        try (MockedStatic<ReviewMapper> mocked = Mockito.mockStatic(ReviewMapper.class)) {
            mocked.when(() -> ReviewMapper.toDto(review)).thenReturn(null); // DTOの結果には関心がないため、nullを返すように設定

            // when
            reviewService.createReview(dto, email);

            // then
            then(reviewRepository).should().save(Mockito.<Review>any()); // 保存操作が呼び出されたことを検証
        }
    }

    @Test
    void レビュー編集_権限あり_成功() {
        // given
        Long reviewId = 1L;
        String email = "user@example.com";

        Review review = mock(Review.class);
        User user = mock(User.class);
        given(review.getUser()).willReturn(user);
        given(user.getEmail()).willReturn(email);
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setContent("編集された内容");
        dto.setRating(4);

        try (MockedStatic<ReviewMapper> mocked = Mockito.mockStatic(ReviewMapper.class)) {
            given(reviewRepository.save(review)).willReturn(review);
            mocked.when(() -> ReviewMapper.toDto(review)).thenReturn(null);

            // when
            reviewService.updateReview(reviewId, dto, email);

            // then
            then(reviewRepository).should().save(review);
        }
    }

    @Test
    void レビュー編集_他人のレビュー_失敗() {
        // given
        Long reviewId = 2L;
        String email = "other@example.com";

        Review review = mock(Review.class);
        User user = mock(User.class);
        given(review.getUser()).willReturn(user);
        given(user.getEmail()).willReturn("owner@example.com"); // 作成者と異なる
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setContent("不正な編集");
        dto.setRating(1);

        // when & then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, dto, email))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED_REVIEW_ACCESS.getMessage());
    }
}
