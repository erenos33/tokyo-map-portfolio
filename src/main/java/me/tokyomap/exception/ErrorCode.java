package me.tokyomap.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * アプリケーション全体で使用されるエラーコードの列挙型
 * 各エラーに対応するHTTPステータスとメッセージキーを定義
 */
@Getter
public enum ErrorCode {

    //ユーザー関連
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user.not.found"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "email.already.exists"),
    EMAIL_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "email.not.verified"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "email.send.failed"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "password.invalid"),

    //レストラン関連
    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "restaurant.not.found"),
    RESTAURANT_ALREADY_REGISTERED(HttpStatus.CONFLICT, "restaurant.already.registered"),

    //レビュー関連
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "review.not.found"),
    UNAUTHORIZED_REVIEW_ACCESS(HttpStatus.FORBIDDEN, "review.unauthorized.access"),
    ALREADY_LIKED_REVIEW(HttpStatus.BAD_REQUEST, "review.already.liked"),
    REVIEW_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "review.like.not.found"),

    //お気に入り関連
    ALREADY_FAVORITED(HttpStatus.CONFLICT, "favorite.already.exists"),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "favorite.not.found"),

    //コメント関連
    UNAUTHORIZED_COMMENT_ACCESS(HttpStatus.FORBIDDEN, "comment.unauthorized.access"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "comment.not.found"),

    //認証関連
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "verification.code.invalid"),
    EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "verification.code.expired"),
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "auth.required"),

    //権限関連
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "access.denied"),

    //外部API関連
    GOOGLE_API_ERROR(HttpStatus.BAD_REQUEST, "google.api.error"),

    //地域関連
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "location.not.found"),

    //その他
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "input.invalid"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");

    private final HttpStatus httpStatus;
    private final String message;

    /**
     * エラーコードと対応するHTTPステータス・メッセージキーを初期化
     */
    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
