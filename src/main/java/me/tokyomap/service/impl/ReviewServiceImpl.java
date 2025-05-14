package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.restaurant.repository.RestaurantRepository;
import me.tokyomap.domain.review.entity.Review;
import me.tokyomap.domain.review.entity.ReviewLike;
import me.tokyomap.domain.review.repository.ReviewLikeRepository;
import me.tokyomap.domain.review.repository.ReviewRepository;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.dto.review.ReviewLikeCountResponseDto;
import me.tokyomap.dto.review.ReviewRequestDto;
import me.tokyomap.dto.review.ReviewResponseDto;
import me.tokyomap.dto.review.ReviewStatisticsResponseDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.mapper.ReviewMapper;
import me.tokyomap.service.ReviewService;
import me.tokyomap.util.EntityFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * レビューに関するビジネスロジックを提供するサービスの実装クラス
 * 投稿、編集、削除、一覧取得、いいね処理、統計集計などを担当
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    /**
     * 新しいレビューを投稿する
     */
    @Override
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto dto, String email) {
        User user = EntityFinder.getUserOrThrow(userRepository, email);
        Restaurant restaurant = EntityFinder.getRestaurantOrThrow(restaurantRepository, dto.getRestaurantId());

        Review review = Review.builder()
                .user(user)
                .restaurant(restaurant)
                .content(dto.getContent())
                .rating(dto.getRating())
                .build();

        Review saved = reviewRepository.save(review);
        return ReviewMapper.toDto(saved);
    }

    /**
     * レビューを編集する（投稿者本人のみ可）
     */
    @Override
    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto dto, String email) {

        Review review = EntityFinder.getReviewOrThrow(reviewRepository, reviewId);

        if(!review.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
        }

        review.setContent(dto.getContent());
        review.setRating(dto.getRating());

        Review updated = reviewRepository.save(review);
        return ReviewMapper.toDto(updated);
    }

    /**
     * レビューを削除する（投稿者本人のみ可）
     */
    @Override
    @Transactional
    public void deleteReview(Long reviewId, String email) {

        Review review = EntityFinder.getReviewOrThrow(reviewRepository, reviewId);

        if(!review.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
        }
        reviewRepository.delete(review);
    }

    /**
     * 指定されたレストランに紐づくレビュー一覧を取得（ページング対応）
     */
    @Override
    public Page<ReviewResponseDto> getReviewsByRestaurant(Long restaurantId, Pageable pageable) {
        EntityFinder.getRestaurantOrThrow(restaurantRepository, restaurantId);

        return reviewRepository.findByRestaurantId(restaurantId, pageable)
                .map(ReviewMapper::toDto);
    }

    /**
     * 指定されたレビューに「いいね」を追加する（重複防止）
     */
    @Override
    @Transactional(readOnly = false)
    public void likeReview(Long reviewId, String email) {
        User user = EntityFinder.getUserOrThrow(userRepository, email);
        Review review = EntityFinder.getReviewOrThrow(reviewRepository, reviewId);

        if (reviewLikeRepository.existsByUserAndReview(user, review)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED_REVIEW);
        }
        reviewLikeRepository.save(new ReviewLike(user, review));
    }

    /**
     * 指定されたレビューへの「いいね」を取り消す
     */
    @Override
    @Transactional(readOnly = false)
    public void unlikeReview(Long reviewId, String email) {
        User user = EntityFinder.getUserOrThrow(userRepository, email);

        Review review = EntityFinder.getReviewOrThrow(reviewRepository, reviewId);

        ReviewLike like = reviewLikeRepository.findByUserAndReview(user, review)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_LIKE_NOT_FOUND));

        reviewLikeRepository.delete(like);

    }

    /**
     * 特定のレビューに対する「いいね」件数を取得する
     */
    @Override
    @Transactional(readOnly = true)
    public ReviewLikeCountResponseDto countLikesByReview(Long reviewId) {
        Review review = EntityFinder.getReviewOrThrow(reviewRepository, reviewId);

        Long count = reviewLikeRepository.countByReview(review);

        return new ReviewLikeCountResponseDto(reviewId, count);
    }

    /**
     * ソート条件付きでレビュー一覧を取得する（ページング対応）
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewsWithSort(Long restaurantId, String sortProperty, Sort.Direction direction, Pageable pageable) {
        return reviewRepository.searchReviewsWithSorting(restaurantId, sortProperty, direction, pageable);
    }

    /**
     * 指定されたレストランのレビュー統計情報（平均スコア・件数）を取得する
     */
    @Override
    @Transactional(readOnly = true)
    public ReviewStatisticsResponseDto getReviewStatistics(Long restaurantId) {
        return reviewRepository.getStatisticsByRestaurantId(restaurantId);
    }

}
