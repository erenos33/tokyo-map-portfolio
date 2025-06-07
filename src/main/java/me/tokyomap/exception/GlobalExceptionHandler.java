package me.tokyomap.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * アプリケーション全体で発生する例外を処理するグローバルハンドラー
 * 各種例外に対応したエラーレスポンスを構築して返却する
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    /**
     * カスタム例外（CustomException）を処理する
     * ロケールに応じたメッセージを返す
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        Locale locale = LocaleContextHolder.getLocale();

        String localizedMessage = messageSource.getMessage(
                errorCode.getMessage(),
                null,
                locale
        );

        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", errorCode.name());
        response.put("message", localizedMessage);

        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    /**
     * その他の予期しない例外を処理する
     * Swaggerアクセス時はそのままスロー、それ以外は500エラーとして処理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri != null && (uri.contains("/v3/api-docs") || uri.contains("/swagger"))) {
            throw new RuntimeException(ex);
        }

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        Locale locale = LocaleContextHolder.getLocale();
        String localizedMessage = messageSource.getMessage(errorCode.getMessage(), null, locale);

        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", errorCode.name());
        response.put("message", localizedMessage);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * バリデーションエラー（@Valid検証失敗）を処理する
     * フィールドごとのエラーメッセージを含むレスポンスを返す
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        Locale locale = LocaleContextHolder.getLocale();
        String localizedMessage = messageSource.getMessage(errorCode.getMessage(), null, locale);

        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", errorCode.name());
        response.put("message", localizedMessage);
        response.put("details", errorMessages);

        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    /**
     * アクセス拒否または認証不足の例外を処理する
     */
    @ExceptionHandler({ AccessDeniedException.class, AuthenticationCredentialsNotFoundException.class })
    public ResponseEntity<Map<String, Object>> handleAccessDenied(Exception ex) {
        ErrorCode errorCode = ErrorCode.AUTH_REQUIRED;
        Locale locale = LocaleContextHolder.getLocale();

        String localizedMessage = messageSource.getMessage(errorCode.getMessage(), null, locale);

        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", errorCode.name());
        response.put("message", localizedMessage);

        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }
}