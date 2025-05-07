package me.tokyomap.service;

import me.tokyomap.dto.location.LocationResponseDto;

import java.util.List;

public interface LocationService {
    List<LocationResponseDto> getAllLocations();

    List<LocationResponseDto> getLocationsByAdminLevel2(String adminLevel2);
}
