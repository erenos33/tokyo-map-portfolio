package me.tokyomap.mapper;

import me.tokyomap.domain.location.entity.Location;
import me.tokyomap.dto.location.LocationResponseDto;

public class LocationMapper {


    public static LocationResponseDto toDto(Location location) {
        return new LocationResponseDto(
                location.getCountry(),
                location.getAdminLevel1(),
                location.getAdminLevel2(),
                location.getLocality(),
                location.getStreetAddress(),
                location.getPostalCode()
        );
    }
}
