package me.tokyomap.service;

import me.tokyomap.dto.location.LocationResponseDto;

import java.util.List;

public interface LocationService {

    /**
     * 전체 지역 정보 조회
     */
    List<LocationResponseDto> getAllLocations();

    /**
     * 특정 adminLevel2 (예: 구/시) 기준 지역 정보 조회
     */
    List<LocationResponseDto> getLocationsByAdminLevel2(String adminLevel2);
}
