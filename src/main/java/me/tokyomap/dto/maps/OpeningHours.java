package me.tokyomap.dto.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class OpeningHours {
    @JsonProperty("weekday_text")
    private List<String> weekdayText;

    @JsonProperty("open_now")
    private boolean openNow;
}