package me.tokyomap.service.impl;

import lombok.RequiredArgsConstructor;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.restaurant.repository.RestaurantRepository;
import me.tokyomap.domain.review.entity.Review;
import me.tokyomap.domain.review.repository.ReviewRepository;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.dto.review.ReviewRequestDto;
import me.tokyomap.dto.review.ReviewResponseDto;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import me.tokyomap.mapper.ReviewMapper;
import me.tokyomap.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    @Transactional
    public void createReview(ReviewRequestDto dto) {

        // TODO: 리뷰에 이미지 첨부 기능 (S3 연동) 확장 고려

        //유저 검증
        User user = userRepository.findById(dto.getUserId())
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
        reviewRepository.save(review);
    }

    @Override
    public Page<ReviewResponseDto> getReviewsByRestaurant(Long restaurantId, Pageable pageable) {

        // TODO: 평점순, 최신순 등 정렬 옵션 추가 가능성 있음

        //DTO 변환
        return reviewRepository.findByRestaurantId(restaurantId, pageable)
                .map(ReviewMapper::toDto); // Page<Review> → Page<ReviewResponseDto>
    }
}
