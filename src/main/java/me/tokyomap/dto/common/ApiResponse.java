package me.tokyomap.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "공통 API 응답 형식")
public class ApiResponse<T> {

    @Schema(description = "상태 코드", example = "200")
    private int status;

    @Schema(description = "메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;

    @Schema(description = "응답 데이터", example = "실제 데이터는 제네릭으로 들어감")
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "요청이 성공적으로 처리되었습니다", data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "요청이 성공적으로 처리되었습니다.", null);
    }
}
