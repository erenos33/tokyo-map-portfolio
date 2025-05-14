package me.tokyomap.dto.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Google Place APIの検索結果レスポンスDTO
 * 店舗リストと次ページ取得用トークンを含む
 */
@Data
@Schema(description = "Google Place API応答DTO")
public class GooglePlaceResponseDto {

    @Schema(description = "検索結果リスト")
    private List<PlaceResult> results;

    @Schema(description = "次ページ取得用トークン", example = "ATtYBwL1PvFJgW...")
    @JsonProperty("next_page_token")
    private String nextPageToken;

 }
