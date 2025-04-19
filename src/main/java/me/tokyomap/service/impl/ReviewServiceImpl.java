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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Override
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto dto, String email) {

        // TODO: 리뷰에 이미지 첨부 기능 (S3 연동) 확장 고려

        //유저 검증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //음식점 검증
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        //Review 엔티티 생성
        Review review = Review.builder()
                .user(user)
                .restaurant(restaurant)
                .content(dto.getContent())
                .rating(dto.getRating())
                .build();

        // TODO: 음식점 삭제 시 리뷰도 함께 삭제되도록 Cascade 설정 고려
        // TODO: 리뷰에 좋아요/댓글 기능 추가 가능성 있음

        //저장
        Review saved = reviewRepository.save(review);

        return ReviewMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto dto, String email) {

        //리뷰 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        //작성자 검증
        if(!review.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
        }

        //내용 수정
        review.setContent(dto.getContent());
        review.setRating(dto.getRating());

        //지정(flush 보장)
        Review updated = reviewRepository.save(review);

        return ReviewMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, String email) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if(!review.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
        }
        reviewRepository.delete(review);
    }

    @Override
    public Page<ReviewResponseDto> getReviewsByRestaurant(Long restaurantId, Pageable pageable) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));



        return reviewRepository.findByRestaurantId(restaurantId, pageable)
                .map(ReviewMapper::toDto);
        // TODO: 평점순, 최신순 등 정렬 옵션 추가 가능성 있음
    }

    @Override
    @Transactional(readOnly = false)
    public void likeReview(Long reviewId, String email) {


        //사용자 인증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //리뷰 존재 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));


        //이미 좋아요 했는지 확인
        if (reviewLikeRepository.existsByUserAndReview(user, review)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED_REVIEW);
        }

        //좋아요 저장
        ReviewLike like = new ReviewLike(user, review);
        reviewLikeRepository.save(like);
    }

    @Override
    @Transactional(readOnly = false)
    public void unlikeReview(Long reviewId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        ReviewLike like = reviewLikeRepository.findByUserAndReview(user, review)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_LIKE_NOT_FOUND));

        reviewLikeRepository.delete(like);

    }

    @Override
    @Transactional(readOnly = true)
    public ReviewLikeCountResponseDto countLikesByReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        Long count = reviewLikeRepository.countByReview(review);

        return new ReviewLikeCountResponseDto(reviewId, count);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewsWithSort(Long restaurantId, String sortProperty, Sort.Direction direction, Pageable pageable) {
        return reviewRepository.searchReviewsWithSorting(restaurantId, sortProperty, direction, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewStatisticsResponseDto getReviewStatistics(Long restaurantId) {
        return reviewRepository.getStatisticsByRestaurantId(restaurantId);
    }

}
