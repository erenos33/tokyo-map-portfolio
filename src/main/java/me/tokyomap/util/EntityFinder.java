package me.tokyomap.util;


import lombok.experimental.UtilityClass;
import me.tokyomap.domain.restaurant.entity.Restaurant;
import me.tokyomap.domain.restaurant.repository.RestaurantRepository;
import me.tokyomap.domain.review.entity.Review;
import me.tokyomap.domain.review.entity.ReviewComment;
import me.tokyomap.domain.review.repository.ReviewCommentRepository;
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
 * 各種エンティティの取得およびページネーション関連のユーティリティクラス
 * - ユーザーやレストランなどのドメインオブジェクトをIDまたはメールで取得
 * - Pageable生成
 * - 現在ログイン中のユーザー情報取得
 */
@UtilityClass
public class EntityFinder {

    /**
     * メールアドレスに基づいてユーザーを取得。存在しない場合は例外をスローする
     */
    public User getUserOrThrow(UserRepository userRepository, String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * レストランIDで検索し、存在しなければ例外をスローする
     */
    public Restaurant getRestaurantOrThrow(RestaurantRepository repository, Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));
    }

    /**
     * ソート条件付きのPageableを生成する
     */
    public Pageable createPageRequest(int page, int size, String sortProperty, Sort.Direction direction) {
        return PageRequest.of(page, size, Sort.by(direction, sortProperty));
    }

    /**
     * 現在ログイン中のユーザーのメールアドレスを取得する
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
    }

    /**
     * レビューIDで検索し、存在しなければ例外をスローする
     */
    public Review getReviewOrThrow(ReviewRepository repository, Long reviewId) {
        return repository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    }

    /**
     * コメントIDで検索し、存在しなければ例外をスローする
     */
    public static ReviewComment getCommentOrThrow(ReviewCommentRepository repository, Long commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
