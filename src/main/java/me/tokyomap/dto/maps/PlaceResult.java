package me.tokyomap.dto.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlaceResult {
    private String name;

    @JsonProperty("formatted_address")
    private String formattedAddress;

    private double rating;
    private Geometry geometry;

}
