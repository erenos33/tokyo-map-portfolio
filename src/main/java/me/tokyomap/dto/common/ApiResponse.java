package me.tokyomap.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * APIの共通レスポンス構造を定義するクラス
 */
@Getter
@AllArgsConstructor
@Schema(description = "共通APIレスポンス形式")
public class ApiResponse<T> {

    @Schema(description = "ステータスコード", example = "200")
    private int status;

    @Schema(description = "メッセージ", example = "リクエストが正常に処理されました。")
    private String message;

    @Schema(description = "レスポンスデータ", example = "実際のデータはジェネリック型で返されます。")
    private T data;

    /**
     * データを含む成功レスポンスを生成する
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "リクエストが正常に処理されました。", data);
    }

    /**
     * データなしの成功レスポンスを生成する
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "リクエストが正常に処理されました。", null);
    }
}
