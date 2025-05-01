package me.tokyomap.dto.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Google Place API 응답 DTO")
public class GooglePlaceResponseDto {

    @Schema(description = "검색 결과 리스트")
    private List<PlaceResult> results;

    @Schema(description = "다음 페이지 토큰", example = "ATtYBwL1PvFJgW...")
    @JsonProperty("next_page_token")
    private String nextPageToken;

 }
