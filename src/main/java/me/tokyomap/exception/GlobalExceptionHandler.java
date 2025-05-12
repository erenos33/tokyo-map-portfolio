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

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        Locale locale = LocaleContextHolder.getLocale();

        String localizedMessage = messageSource.getMessage(
                errorCode.getMessage(), // 메시지 키
                null,
                locale
        );

        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", errorCode.name());
        response.put("message", localizedMessage);

        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

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