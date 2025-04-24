package me.tokyomap.dto.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GooglePlaceResponseDto {

    private List<PlaceResult> results;

    @JsonProperty("next_page_token")
    private String nextPageToken;
 }
