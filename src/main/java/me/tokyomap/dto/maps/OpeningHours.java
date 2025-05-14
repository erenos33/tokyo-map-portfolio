package me.tokyomap.dto.maps;

import  com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * レストランなどの営業時間情報を表すDTO
 * 曜日ごとの営業時間テキストと現在の営業中フラグを含む
 */
@Data
public class OpeningHours {
    @JsonProperty("weekday_text")
    private List<String> weekdayText;

    @JsonProperty("open_now")
    private boolean openNow;
}