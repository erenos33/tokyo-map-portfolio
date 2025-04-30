package me.tokyomap.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    //✅ 유저 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user.not.found"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "email.already.exists"),
    EMAIL_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "email.not.verified"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "email.send.failed"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "password.invalid"),

    // ✅ 음식점 관련
    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "restaurant.not.found"),
    RESTAURANT_ALREADY_REGISTERED(HttpStatus.CONFLICT, "restaurant.already.registered"),

    // ✅ 리뷰 관련
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "review.not.found"),
    UNAUTHORIZED_REVIEW_ACCESS(HttpStatus.FORBIDDEN, "review.unauthorized.access"),
    ALREADY_LIKED_REVIEW(HttpStatus.BAD_REQUEST, "review.already.liked"),
    REVIEW_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "review.like.not.found"),

    // ✅ 즐겨찾기 관련
    ALREADY_FAVORITED(HttpStatus.CONFLICT, "favorite.already.exists"),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "favorite.not.found"),

    // ✅ 댓글 관련
    UNAUTHORIZED_COMMENT_ACCESS(HttpStatus.FORBIDDEN, "comment.unauthorized.access"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "comment.not.found"),

    // ✅ 인증 관련
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "verification.code.invalid"),
    EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "verification.code.expired"),

    // ✅ 권한 관련
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "access.denied"),

    // ✅ 기타
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "input.invalid"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
