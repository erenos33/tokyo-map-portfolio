package me.tokyomap.util;


import lombok.experimental.UtilityClass;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.restaurant.repository.RestaurantRepository;
import me.tokyomap.domain.review.entity.Review;
import me.tokyomap.domain.review.repository.ReviewRepository;
import me.tokyomap.domain.user.entity.User;
import me.tokyomap.domain.user.repository.UserRepository;
import me.tokyomap.exception.CustomException;
import me.tokyomap.exception.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 공통 Entity 조회 및 페이지 처리 유틸
 * - 유저, 음식점 등 도메인 객체 조회
 * - Pageable 정리
 * - 현재 로그인 유저 이메일 조회
 */
@UtilityClass
public class EntityFinder {

    /**
     * 이메일로 유저 조회 후 없으면 예외
     */

    public User getUserOrThrow(UserRepository userRepository, String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 음식점 ID로 조회 후 없으면 예외
     */
    public Restaurant getRestaurantOrThrow(RestaurantRepository repository, Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));
    }

    /**
     * 정렬 가능한 Pageable 생성기
     */
    public Pageable createPageRequest(int page, int size, String sortProperty, Sort.Direction direction) {
        return PageRequest.of(page, size, Sort.by(direction, sortProperty));
    }

    /**
     * 현재 로그인한 사용자의 이메일 반환
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
    }

    public Review getReviewOrThrow(ReviewRepository repository, Long reviewId) {
        return repository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    }
}
