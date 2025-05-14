package me.tokyomap.exception;

/**
 * アプリケーション内で使用されるカスタム例外クラス
 * 定義されたエラーコード（ErrorCode）に基づいて例外情報を提供する
 */
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 指定されたエラーコードに基づいて例外を生成
     * エラーメッセージは ErrorCode から取得
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 発生したエラーの ErrorCode を返す
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
